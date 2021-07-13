package com.example.workmanagerexample


import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.example.workmanagerexample.databinding.ActivityMainBinding
import com.example.workmanagerexample.worker.ImageDownloadWorker
import com.example.workmanagerexample.worker.NotificationWorker
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val workManager by lazy {
        WorkManager.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.download.setOnClickListener {
            createOneTimeRequest()
        }
    }

    private fun createOneTimeRequest() {
        binding.progressBar.visibility = View.VISIBLE
        //Adding constraints for worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        //Adding data to give input to worker
        val data = Data.Builder().putString(
            "imageUrl",
            "https://image.shutterstock.com/image-photo/tree-against-sky-on-tranquil-260nw-434350822.jpg"
        ).build()

        //One time worker initialization
        val imageWorker = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("imageWork")
            .build()

        //Another One Time Worker
        val notificationWork= OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString("notificationTitle","Image Downloaded").build())
            .addTag("notificationWork")
            .build()

        //Chaining 1-->2 both the workers
        workManager.beginWith(imageWorker).then(notificationWork).enqueue()

        //observing imageWorker
        observeWork(imageWorker.id)
    }

    private fun observeWork(id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this, { workInfo ->

            //check if worker1 finished its task
            if (workInfo != null && workInfo.state.isFinished) {
                binding.progressBar.visibility = View.GONE
                val uriResult = workInfo.outputData.getString("IMAGE_URI")
                if (uriResult != null) {
                   binding.downloadedImage.setImageURI(Uri.parse(uriResult))
                    binding.textView.visibility=View.GONE
                }
            }

        })
    }
}