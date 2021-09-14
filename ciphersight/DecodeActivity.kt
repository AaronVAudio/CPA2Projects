package com.example.ciphersight

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class DecodeActivity : AppCompatActivity() {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var spinnerFirst: Spinner
    private lateinit var spinnerSecond: Spinner
    private lateinit var textViewFirst: TextView
    private lateinit var textViewSecond: TextView
    private lateinit var input: EditText
    private lateinit var output: EditText
    private lateinit var decodeType: RadioGroup
    private lateinit var utilities: CipherUtilities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode)

        if (supportActionBar != null)
            supportActionBar?.hide()

        spinnerFirst = findViewById(R.id.spinnerFirstD)
        spinnerSecond = findViewById(R.id.spinnerSecondD)
        input = findViewById(R.id.editTextInputD)
        output = findViewById(R.id.editTextOutputD)
        decodeType = findViewById(R.id.radioGroupDecode)
        textViewFirst = findViewById(R.id.textViewFirstD)
        textViewSecond = findViewById(R.id.textViewSecondD)
        utilities = CipherUtilities()

        textViewFirst.visibility = View.INVISIBLE
        textViewSecond.visibility = View.INVISIBLE
        spinnerFirst.visibility = View.INVISIBLE
        spinnerSecond.visibility = View.INVISIBLE

        input.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        output.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO

        ArrayAdapter.createFromResource(
                this,
                R.array.alphabet,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFirst.adapter = adapter
            spinnerSecond.adapter = adapter
        }
    }

    fun onButtonClick(view: View) {
        when (view.id) {
            R.id.buttonReturnD -> finishAfterTransition()
            R.id.buttonDecodeNow -> decode()
            R.id.buttonSwapUpD -> {
                input.text = output.text
                output.text = Editable.Factory.getInstance().newEditable("")
            }
            R.id.buttonLoadImageD -> selectImage(this)
        }
    }

    private fun decode() {
        var decodeSelected = decodeType.checkedRadioButtonId
        when (decodeSelected) {
            R.id.radioButtonCaesarD -> {
                var firstLetter = spinnerFirst.selectedItemPosition
                var secondLetter = spinnerSecond.selectedItemPosition
                var offset = 0
                if (firstLetter > secondLetter)
                    offset = firstLetter - secondLetter
                else if (secondLetter > firstLetter)
                    offset = secondLetter - firstLetter
                var inText = input.text.toString()
                var outText = utilities.caesarDecode(inText, offset)
                output.text = Editable.Factory.getInstance().newEditable(outText)
            }
            R.id.radioButtonBinaryD -> {
                var inText = input.text.toString()
                var outText = utilities.binaryDecode(inText)
                output.text = Editable.Factory.getInstance().newEditable(outText)
            }
            R.id.radioButtonHexD -> {
                var inText = input.text.toString()
                var outText = utilities.hexDecode(inText)
                output.text = Editable.Factory.getInstance().newEditable(outText)
            }
        }
    }

    fun onCipherClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.id) {
                R.id.radioButtonCaesarD -> {
                    if (checked) {
                        textViewFirst.visibility = View.VISIBLE
                        textViewSecond.visibility = View.VISIBLE
                        spinnerFirst.visibility = View.VISIBLE
                        spinnerSecond.visibility = View.VISIBLE
                    } else {
                        textViewFirst.visibility = View.INVISIBLE
                        textViewSecond.visibility = View.INVISIBLE
                        spinnerFirst.visibility = View.INVISIBLE
                        spinnerSecond.visibility = View.INVISIBLE
                    }
                }
                R.id.radioButtonBinaryD -> {
                    textViewFirst.visibility = View.INVISIBLE
                    textViewSecond.visibility = View.INVISIBLE
                    spinnerFirst.visibility = View.INVISIBLE
                    spinnerSecond.visibility = View.INVISIBLE
                }
                R.id.radioButtonHexD -> {
                    textViewFirst.visibility = View.INVISIBLE
                    textViewSecond.visibility = View.INVISIBLE
                    spinnerFirst.visibility = View.INVISIBLE
                    spinnerSecond.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        verifyStoragePermissions(this)

        builder.setTitle("Choose An Image")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.extras!!["data"] as Bitmap?
                    if (selectedImage != null)
                        utilities.processImage(selectedImage, input)
                }
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor: Cursor? = contentResolver.query(selectedImage,
                                filePathColumn, null, null, null)
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath: String = cursor.getString(columnIndex)
                            val image = BitmapFactory.decodeFile(picturePath)
                            if (image != null)
                                utilities.processImage(image, input)
                            cursor.close()
                        }
                    }
                }
            }
        }
    }

    private fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }
}