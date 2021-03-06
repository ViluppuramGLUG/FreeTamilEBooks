package com.jskaleel.fte.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.jskaleel.fte.BuildConfig
import com.jskaleel.fte.R
import com.jskaleel.fte.utils.AppPreference
import com.jskaleel.fte.utils.AppPreference.get
import com.jskaleel.fte.utils.AppPreference.set
import com.jskaleel.fte.utils.Constants
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    private lateinit var mContext: Context

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar.setNavigationOnClickListener {
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }

        ivShare.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "இந்த செயலிமூலம் மின்நூல்களை இலவசமாக தரவிறக்கி படிக்கமுடிகிறது. நீங்களும் முயற்சித்து பார்க்கவும். http://bit.ly/FTEAndroid")
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        val isPushChecked = AppPreference.customPrefs(mContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE, true]

        swPush.isChecked = isPushChecked
        txtPushStatus.text = if (isPushChecked) getString(R.string.on) else getString(R.string.off)

        swPush.setOnCheckedChangeListener { _, isChecked ->
            AppPreference.customPrefs(mContext)[Constants.SharedPreference.NEW_BOOKS_UPDATE] = isChecked
            txtPushStatus.text = if (isChecked) getString(R.string.on) else getString(R.string.off)

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.CHANNEL_NAME)
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.CHANNEL_NAME)
            }
        }

        rlOSSLayout.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_sources))
            startActivity(Intent(mContext, OssLicensesMenuActivity::class.java))
        }
        rlSourceCodeLayout.setOnClickListener {
            val url = "https://github.com/khaleeljageer/FreeTamilEBooks"
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            }
            startActivity(shareIntent)
        }

        txtAppVersion.text = String.format(getString(R.string.version, BuildConfig.VERSION_NAME))
    }
}