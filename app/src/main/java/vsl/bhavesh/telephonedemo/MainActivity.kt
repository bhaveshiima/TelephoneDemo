package vsl.bhavesh.telephonedemo

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Runtime Permission [ START ]
        var status = ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.SEND_SMS)

        // Permission not granted
        if (status==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE),0    )
        }
        // Runtime Permission [ END ]


        // Send SMS [ START ]
        sendsms.setOnClickListener {

            var sintent = Intent(this@MainActivity,SendActivity::class.java)
            var dintent = Intent(this@MainActivity,DeliverActivity::class.java)

            var psintent = PendingIntent.getActivity(this@MainActivity,0,sintent,0)
            var dsintent = PendingIntent.getActivity(this@MainActivity,0,dintent,0)


            var sManager = SmsManager.getDefault()
            sManager.sendTextMessage(et1.text.toString(), null, et2.text.toString(),psintent,dsintent)

        } // Send SMS [ END ]


        // Call [ START ]
        call.setOnClickListener {
            var i = Intent()
            i.action = Intent.ACTION_CALL
            i.data = Uri.parse("tel:"+et1.text.toString())
            startActivity(i)
        }
        // Call [ END ]


    } // onCreate function


    // Override Request Permission [ START ]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Toast Message display
        if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this,"You Cann't send SMS and CALL. First you need to Allow Permission",Toast.LENGTH_LONG).show()
        }
    }
    // Override Request Permission [ END ]


} // Main Activity


