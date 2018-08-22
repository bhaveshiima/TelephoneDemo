package vsl.bhavesh.telephonedemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Runtime Permission [ START ]
        var status = ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.SEND_SMS)

        // Permission not granted
        if (status==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.SEND_SMS,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA),0    )
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


        //Call attachment action [ START ]
        attach.setOnClickListener {

            // when user click on this button then need to open popup for selecting file - camera or file
            // So display the dialog box for that
            var alert_dialog = AlertDialog.Builder(this@MainActivity) //create object for Alert Dilog box

            alert_dialog.setTitle("Message")  //set Title
            alert_dialog.setMessage("Choose an option") //set Option
            // allow maximum 3 button , positive,negative
            // For Camera Button
            alert_dialog.setPositiveButton("CAMERA", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        // open the camera
                        var i = Intent("android.media.action.IMAGE_CAPTURE") //open predefine activity
                        startActivityForResult(i,123) // request code - any positive number
                    }

            })
            // For Gallery Button
            alert_dialog.setNegativeButton("GALLERY", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {

                    var i = Intent() //create object
                    i.action = Intent.ACTION_GET_CONTENT //get all the list of file on action
                    i.type = "*/*"  //set the specific time..
                    startActivityForResult(i,124) // use different request code if you use multile

                }

            })

            alert_dialog.show() //Display the alert dialog


        }
        // Call Attachment [ END ]




        //Send Email [ START ]
        sendemail.setOnClickListener {

            var i = Intent()  //create intent object
            i.action = Intent.ACTION_SEND  //set action

            // to send morethan one value to pass into next activity we used the putExtra method
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf(et3_email.text.toString())) //to whome you need to send

            i.putExtra(Intent.EXTRA_SUBJECT,et4_subject.text.toString()) // enter subject of email
            i.putExtra(Intent.EXTRA_TEXT,et5_body.text.toString()) // email body

            // For attachment, which file you want to send as an attachment
            i.putExtra(Intent.EXTRA_STREAM, uri_obj) //define url_obj which file you want to an attachment

            // Now set the MIME type for attachement
            i.type = "message/rfc822" // enable the MIME (Multimedia Internet Mail Extension) Type data

            startActivity(i) //start activity by passing intent

        }
        //Send Email [ END ]





        // Java Email Send using AsyncTask
        javamail.setOnClickListener {

            var lop = LongOperation(et3_email.text.toString(),
                    et4_subject.text.toString(),et5_body.text.toString())

            lop.execute()
        }

    } // onCreate function


    var uri_obj:Uri? = null //define global variable
    // Override the function OnActivityResult Once result completed this method call..
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //If we have multiple request code then we can separate using that code
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            //Camera... now if user capture image or cancel image you can get from result code

            var bmp:Bitmap = data!!.extras.get("data") as Bitmap
            uri_obj = getImageUri(this@MainActivity,bmp)

        }else if (requestCode == 124 && resultCode == Activity.RESULT_OK){
            // Gallery
            uri_obj = data!!.data //by calling intent object you will get url object

        }

    }

    // Get Image URI from Bitmap [ START ]
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null)
        return Uri.parse(path)
    }
   //Get Image URI from Bitmap [ END ]



    // Override Request Permission [ START ]
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Toast Message display
        if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED && grantResults[2] == PackageManager.PERMISSION_DENIED && grantResults[3] == PackageManager.PERMISSION_DENIED && grantResults[4] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this,"You Cann't send SMS and CALL. First you need to Allow Permission",Toast.LENGTH_LONG).show()
        }

        /*
        if (grantResults[2] == PackageManager.PERMISSION_DENIED && grantResults[3] == PackageManager.PERMISSION_DENIED && grantResults[4] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this,"You Cann't send Email & Use Camera. First you need to Allow Permission",Toast.LENGTH_LONG).show()
        }
        */
    }
    // Override Request Permission [ END ]


} // Main Activity


