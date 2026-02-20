//
//  APWallet.swift
//  BancoPosta
//
//  Created by Joël Gerbore on 06/07/21.
//  Copyright © 2021 Poste Italiane S.P.A. All rights reserved.
//

import CoreNFC
import Foundation
import PassKit
import WatchConnectivity

protocol APWalletProtocol {
    var watchPresent: Bool { get set }
    var activeTokenIphone: APRegisteredToken? { get set }
    var activeTokenWatch: APRegisteredToken? { get set }

    func deviceSupportAddToWallet() -> Bool

    func localPassFor(_ activeTokens: [APRegisteredToken]) -> PKPass?
    func remotePassFor(_ activeTokens: [APRegisteredToken]) -> PKPass?
    func getCardFPAN(_ cardSuffix: String?) -> String?
    func getRequireActivationPasses() -> [PKPass]
    func getRequireActivationPassForService(passTypeIdentifier: String?,
                                            serialNumber: String?) -> [InAppVerificationCard]
    func matchLocalPass(dPanSuffix: String?,
                        fPanSuffix: String?,
                        onlyRequiresActivationPasses: Bool) -> PKPass?
    func matchRemotePass(dPanSuffix: String?,
                         fPanSuffix: String,
                         onlyRequiresActivationPasses: Bool) -> PKPass?
}

public class APWallet: NSObject, APWalletProtocol, WCSessionDelegate {
    var watchPresent: Bool = false
    private let watchSession: WCSession?

    var activeTokenIphone: APRegisteredToken?
    var activeTokenWatch: APRegisteredToken?
    let passKit = PKPassLibrary()

    override public init() {
        watchSession = WCSession.isSupported() ? WCSession.default : nil
        super.init()
        watchSession?.delegate = self
        watchPresent = watchSession?.isPaired ?? watchPresent
        watchSession?.activate()
    }

    // MARK: - Can add checker

    /**
     Returns whether the device supports adding passes.
     */
    @objc
    public class func canAddPassToWallet() -> Bool {
        return PKPassLibrary.isPassLibraryAvailable() &&
            PKAddPaymentPassViewController.canAddPaymentPass()
    }

    public func hasAtLeastOnePass() -> Bool {
        let localPassesPresent = !passKit.passes().isEmpty
        let remotePassesPresent: Bool

        if #available(iOS 13.4, *) {
            remotePassesPresent = !passKit.remoteSecureElementPasses.isEmpty
        } else {
            remotePassesPresent = !passKit.remotePaymentPasses().isEmpty
        }

        return localPassesPresent || remotePassesPresent
    }

    func deviceSupportAddToWallet() -> Bool {
        return APWallet.canAddPassToWallet()
    }

    // MARK: - Check card is in wallet for iPhone

    func localPassFor(_ activeTokens: [APRegisteredToken]) -> PKPass? {
        let localPasses = passKit.passes()

        for pass in localPasses {
            if let token = activeTokens.first(where: { $0.dpanid == pass.dpan && $0.fpanid == pass.fpan }) {
                activeTokenIphone = token
                return pass
            }
        }
        return nil
    }

    // MARK: - Check card is in wallet for apple watch

    func remotePassFor(_ activeTokens: [APRegisteredToken]) -> PKPass? {
        if #available(iOS 13.4, *) {
            let remotePassed = passKit.remoteSecureElementPasses

            for pass in remotePassed {
                if let token = activeTokens.first(where: { $0.dpanid == pass.dpan && $0.fpanid == pass.fpan }) {
                    activeTokenWatch = token
                    return pass
                }
            }
        } else {
            let remotePassed = passKit.remotePaymentPasses()
            for pass in remotePassed {
                if let token = activeTokens.first(where: { $0.dpanid == pass.dpan && $0.fpanid == pass.fpan }) {
                    activeTokenWatch = token
                    return pass
                }
            }
        }

        return nil
    }

    func getCardFPAN(_ cardSuffix: String?) -> String? {
        var paymentPasses = passKit.passes()
        for pass in paymentPasses {
            let paymentPass = pass.paymentPass
            if paymentPass?.primaryAccountNumberSuffix == cardSuffix {
                return paymentPass?.primaryAccountIdentifier
            }
        }
        if WCSession.isSupported() {
            // check if the device support to handle an Apple Watch
            if let watchSession, watchSession.isPaired {
                // Check if the iPhone is paired with the Apple Watch
                paymentPasses = passKit.remotePaymentPasses()
                for pass in paymentPasses {
                    let paymentPass = pass.paymentPass
                    if paymentPass?.primaryAccountNumberSuffix == cardSuffix {
                        return paymentPass?.primaryAccountIdentifier
                    }
                }
            }
        }
        return nil
    }

    // MARK: - WCSessionDelegate

    public func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        watchPresent = session.isPaired
    }

    public func sessionDidBecomeInactive(_ session: WCSession) {}

    public func sessionDidDeactivate(_ session: WCSession) {}

    deinit {
        OneAppLog.verbose("\(String(describing: self)) deinit called")
    }
}

extension APWallet: LoggedUserReader {}
