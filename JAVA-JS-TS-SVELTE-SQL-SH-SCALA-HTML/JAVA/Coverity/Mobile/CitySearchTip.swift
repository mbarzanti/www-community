//
//  CitySearchTip.swift
//  OneApp
//
//  Created by Ruffolo Antonio on 27/10/21.
//

import Foundation

final class CitySearchTip: NSObject {
    @objc dynamic var text: String = ""

    static func == (lhs: CitySearchTip, rhs: CitySearchTip) -> Bool {
        return lhs.text == rhs.text
    }

    deinit {
        OneAppLog.debug("\(String(describing: self)) deinit called")
    }
}

extension CitySearchTip: TextTip {}

@objc
protocol TextTip {
    dynamic var text: String { get set }
}

typealias TextObject = TextTip & NSObject
