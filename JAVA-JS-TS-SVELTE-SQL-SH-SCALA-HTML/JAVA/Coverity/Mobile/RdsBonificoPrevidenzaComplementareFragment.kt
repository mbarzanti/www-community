package it.posteitaliane.df_rds.sections.bonifico.previdenzaComplementare

import android.os.Bundle
import android.text.InputFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import it.posteitaliane.df_rds.R
import it.posteitaliane.df_rds.RdsFaqTag
import it.posteitaliane.df_rds.RdsViewModel
import it.posteitaliane.df_rds.databinding.FragmentRdsBonificoPrevidenzaComplementareBinding
import it.posteitaliane.df_rds.uimodel.RdsMeseAnnoUiModel
import it.posteitaliane.posteuikit.analytics.AnalyticsConstantsEngine.ANALYTICS_TRANSACTION_ID
import it.posteitaliane.df_sessionmanager.analytics.AnalyticsRegion
import it.posteitaliane.df_sessionmanager.analytics.initAnalytics
import it.posteitaliane.posteuikit.uimodel.FaqData
import it.posteitaliane.df_utils.extensions.amountWithEur
import it.posteitaliane.df_utils.extensions.capitalized
import it.posteitaliane.df_utils.extensions.openFaqActivity
import it.posteitaliane.posteuikit.uimodel.VoipSection
import it.posteitaliane.mvvmtoolkit.livedatautils.Consumable.Companion.consumeIfAvailable
import it.posteitaliane.mvvmtoolkit.navigation.NavigationFragment
import it.posteitaliane.posteuikit.toolbars.PostePannelToolbarHelper
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RdsBonificoPrevidenzaComplementareFragment :
    NavigationFragment<FragmentRdsBonificoPrevidenzaComplementareBinding>(R.layout.fragment_rds_bonifico_previdenza_complementare),
    PostePannelToolbarHelper.PostePannelToolbarListeners {

    override val viewModel by viewModel<RdsBonificoPrevidenzaComplementareViewModel>()
    private val sharedViewModel by activityViewModel<RdsViewModel>()

    private var selectedMeseAnnoUiModel: RdsMeseAnnoUiModel? = null

    override var toolbarHelper: PostePannelToolbarHelper? = null

    override fun onViewReady() {
        initToolbar()
        setView()
        setViewListeners()
        setObservers()
        errorHandling()
        initAnalytics(
            AnalyticsRegion.RDS_INSERT_DATA_BONIFICO_PREVIDENZA_COMPLEMENTARE,
            bundleOf(ANALYTICS_TRANSACTION_ID to sharedViewModel.analyticsTransactionId)
        )
    }

    override fun initToolbar() {
        toolbarHelper = PostePannelToolbarHelper(
            binding.toolbar,
            this,
            activity as AppCompatActivity,
            enableStepper = true
        )

        toolbarHelper?.apply {
            showBack()
            showClose()
            showHelp()
            setPrimaryTitle(getString(R.string.rds_bonifico_previdenza_complementare_title))
            enableStepper(Pair(1, 2))
        }
    }

    private fun setView() {
        initMeseAnnoSelection()

        with(binding) {
            vm = viewModel
        }
        binding.etCf.binding.posteTextfield.filters += InputFilter.AllCaps()
    }

    private fun getYears(cY: Int): List<Int> {
        var currentYear = cY
        val list = mutableListOf<Int>()
        for (i in 0..10) list.add(currentYear++)
        return list
    }

    private fun initMeseAnnoSelection() {
        val monthList = resources.getStringArray(R.array.wire_transfer_months)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val yearList = getYears(currentYear)

        val list = mutableListOf<RdsMeseAnnoUiModel>()

        yearList.forEach { y ->
            monthList.forEachIndexed { index, m ->
                if (!(currentYear == y && currentMonth > index)) {
                    list.add(
                        RdsMeseAnnoUiModel(
                            "${m.capitalized()} $y",
                            m, y.toString()
                        )
                    )
                }
            }
        }

        val adapter = RdsMeseAnnoAdapter(
            requireContext(),
            it.posteitaliane.posteuikit.R.layout.poste_autocomplete_dropdown_textview,
            ArrayList(list)
        )

        viewModel.rdsBonificoFormModel.bonificoPrevidenzaComplementare.periodo.let {
            binding.etPeriodo.setText(
                it.label
            )
        }

        binding.etPeriodo.setAutocompleteAdapter(adapter)
        binding.etPeriodo.setCustomOnAutocompleteItemClickListener { _, _, position, _ ->
            binding.etPeriodo.setText(adapter.getItem(position).label)
            selectedMeseAnnoUiModel = adapter.getItem(position)
        }

        binding.etPeriodo.showDropDownOnClick()
    }

    private fun setViewListeners() {

        lifecycleScope.launchWhenStarted {
            binding.etImporto.amountWithEur()
        }

        viewModel.amountErrorField.observe(viewLifecycleOwner) {
            binding.etImporto.error = if (it == 0) null else getString(it)
        }

        binding.btnContinua.setOnClickListener {
            if (viewModel.validate(requireActivity())) {
                viewModel.buttonGoToStepTwoClicked(
                    binding.etImporto.getText()?.toString()?.replace("€", "") ?: "",
                    binding.etCausale.getText().toString(),
                    binding.etNomeCognome.getText().toString(),
                    binding.etCf.getText().toString().uppercase(),
                    selectedMeseAnnoUiModel?.mese ?: "",
                    selectedMeseAnnoUiModel?.anno ?: ""
                )
                viewModel.rdsBonificoFormModel.bonificoPrevidenzaComplementare.periodo =
                    selectedMeseAnnoUiModel ?: RdsMeseAnnoUiModel()
            }
        }
    }

    private fun errorHandling() {
        viewModel.addItemsToValidator(
            requireContext(),
            listOf(
                binding.etImporto,
                binding.etNomeCognome,
                binding.etCf
            )
        )
    }

    private fun setObservers() {
        viewModel.amountErrorField.observe(viewLifecycleOwner) {
            binding.etImporto.error = if (it == 0) null else getString(it)
        }
        viewModel.nameErrorField.observe(viewLifecycleOwner) {
            binding.etNomeCognome.error = if (it == 0) null else getString(it)
        }
        viewModel.fiscalCodeErrorField.consumeIfAvailable(viewLifecycleOwner) {
            binding.etCf.error = if (it == 0) null else getString(it)
        }

        viewModel.periodoErrorField.observe(viewLifecycleOwner) {
            binding.etPeriodo.error = if (it == 0) null else getString(it)
        }
    }

    override fun onPostePannelBackClick() {
        requireActivity().onBackPressed()
    }

    override fun onPostePannelCloseClick() {
        sharedViewModel.handleCloseDialog()
    }

    override fun onPostePannelHelpClick() {
        openFaqActivity(FaqData(tagAdobe = RdsFaqTag.INSERT_DATA_BONIFICO_PREVIDENZA_COMPLEMENTARE_SECTION, section = VoipSection.Finanziario))
    }

    override fun handleArguments(arguments: Bundle) = Unit // nothing to do here

    override fun handleSavedInstanceState(savedInstanceState: Bundle) {} // nothing to do here
}
