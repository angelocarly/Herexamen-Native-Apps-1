package be.magnias.stahb.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.model.Resource
import be.magnias.stahb.model.Status
import be.magnias.stahb.model.Tab
import be.magnias.stahb.ui.viewmodel.TabViewModel
import be.magnias.stahb.ui.viewmodel.TabViewModelFactory
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.fragment_tab.view.*

/**
 * The tab fragment.
 * In this fragment a single tab is displayed along with it's details.
 */
class TabFragment : Fragment() {

    private lateinit var tabViewModel: TabViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the tab id from the parameters
        val id = getTabId()

        // Init viewmodel
        tabViewModel = ViewModelProviders.of(this, TabViewModelFactory(id)).get(TabViewModel::class.java)

        // Show the correct views if the viewmodel is loading data
        tabViewModel.getLoadingVisibility().observe(this, Observer {loadingVisible ->
            if(loadingVisible) {
                loading_panel.visibility = View.VISIBLE
                checkbox_favorite.visibility = View.GONE
            }
            else {
                loading_panel.visibility = View.GONE
                checkbox_favorite.visibility = View.VISIBLE
            }
        })

        // Show the tab's data when the viewmodel returns a tab
        tabViewModel.getTab().observe(this, Observer<Resource<Tab>> {
            if (it.status == Status.SUCCESS) {
                //Set tab data
                val tab = it.data!!
                tab_text.text = tab.tab
                val title = "${tab.artist} - ${tab.song}"
                tab_title.text = title
                tab_tuning.text = tab.tuning

                checkbox_favorite.isChecked = tab.favorite

            } else if (it.status == Status.ERROR) {
                tab_error.visibility = View.VISIBLE
            }
        })

        // Enable the fragment's toolbar
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_tab, container, false)

        // Favorite button listener
        view.checkbox_favorite.setOnClickListener {

            //Add or remove a tab from favorites
            if(view.checkbox_favorite.isChecked) {
                tabViewModel.addToFavorite()
            }
            else {
                tabViewModel.removeFromFavorite()
            }
        }

        return view
    }

    /**
     * Retrieve the tab's id from the fragment parameters
     * @throws IllegalArgumentException if no id is found in the fragment
     */
    private fun getTabId(): String {
        val id: String?
        if (arguments != null) {
            id = arguments!!.getString("id")

            if (id.isNullOrBlank()) {
                throw IllegalArgumentException("TabFragment requires an id")
            }

        } else {
            throw IllegalArgumentException("TabFragment requires an id")
        }

        return id
    }

    // Instance method
    companion object {

        /**
         * Create a new TabFragment instance
         * @param id The id of the tab that should be displayed
         */
        fun newInstance(id: String): TabFragment {

            val frag = TabFragment()

            // Store the id in the fragment arguments
            val args = Bundle()
            args.putString("id", id)
            frag.arguments = args

            return frag
        }
    }

}
