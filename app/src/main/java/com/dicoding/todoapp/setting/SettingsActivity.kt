package com.dicoding.todoapp.setting

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var workManager: WorkManager
        private lateinit var periodicWorkRequest: PeriodicWorkRequest

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            workManager = WorkManager.getInstance(requireContext())

            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { _, newValue ->
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if(newValue == true){
                    startPeriodicTask()
                }
                else{
                    try {
                        workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
                            .observe(viewLifecycleOwner) { workInfo ->
                                val status = workInfo.state.name
                                Log.d(TAG, "Status : $status")
                                if (workInfo.state == WorkInfo.State.ENQUEUED) {
                                    cancelPeriodicTask()
                                    Toast.makeText(requireContext(),
                                        " remind me later",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    }catch (e: Exception){
                        Log.e(TAG, "Reason : ${e.message}")
                    }
                }
                true
            }

        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }

        private fun startPeriodicTask() {
            val workManager = workManager
            val data = Data.Builder()
                .putString(NOTIFICATION_CHANNEL_ID, "notification_id_reminder")
                .putString(NOTIFICATION_CHANNEL_ID, getString(R.string.notify_content))
                .build()

            periodicWorkRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                1, TimeUnit.DAYS
            ).setInputData(data).build()

            workManager.enqueue(periodicWorkRequest)
            workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id)
                .observe(viewLifecycleOwner) { workInfo ->
                    val status = workInfo.state.name
                    Log.d(TAG, "Status : $status")
                    if (workInfo.state == WorkInfo.State.ENQUEUED) {
                        Log.d(TAG, "Reminder has been enqueued")
                    }
                }
        }

        private fun cancelPeriodicTask() {
            try {
                //workManager.cancelWorkById(periodicWorkRequest.id)
                workManager.cancelAllWork()
            }catch (e : Exception){
                Log.e(TAG, "Failed because : ${e.message}")
            }
        }
    }
}