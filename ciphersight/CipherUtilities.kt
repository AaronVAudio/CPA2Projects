package com.example.ciphersight

import android.graphics.Bitmap
import android.text.Editable
import android.util.Log
import android.widget.EditText
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition


class CipherUtilities {
    fun caesarEncode(text: String, amount: Int): String {
        val offset = amount % 26

        if (offset == 0) return text

        var letterOut: Char

        val chars = CharArray(text.length)

        for ((index, letterIn) in text.withIndex()) {
            if (letterIn in 'A'..'Z') {
                letterOut = letterIn + offset
                if (letterOut > 'Z') letterOut -= 26
            }
            else if (letterIn in 'a'..'z') {
                letterOut = letterIn + offset
                if (letterOut > 'z') letterOut -=26
            }
            else
                letterOut = letterIn
            chars[index] = letterOut
        }

        return chars.joinToString("")
    }

    fun caesarDecode(text: String, offset: Int): String {
        return caesarEncode(text, 26 - offset)
    }

    fun binaryEncode(text: String): String {
        var output: String = ""

        for (c in text) {
            var charNum = c.toInt()
            var outBin = String.format("%" + 8 + "s", charNum.toString(2)).replace(" ".toRegex(), "0")
            output += "$outBin "
        }

        return output
    }

    fun binaryDecode(text: String): String {
        var output: String = ""
        var binLetter: String = ""

        for (c in text) {
            if (c == 'O')
                binLetter += '0'
            else if (c in '0'..'1')
                binLetter += c
            else {
                var counter = 1
                var charInt = 0
                binLetter = binLetter.reversed()
                for (i in binLetter) {
                    charInt += Character.getNumericValue(i) * counter
                    counter *= 2
                }
                output += charInt.toChar()
                binLetter = ""
            }
        }

        if (binLetter != "") {
            var counter = 1
            var charInt = 0
            binLetter = binLetter.reversed()
            for (i in binLetter) {
                charInt += Character.getNumericValue(i) * counter
                counter *= 2
            }
            output += charInt.toChar()
            binLetter = ""
        }

        return output
    }

    fun hexEncode(text: String): String {
        var output: String = ""

        for (c in text) {
            var charNum = c.toInt();
            var hex = Integer.toHexString(charNum)
            output += "$hex "
        }

        return output
    }

    fun hexDecode(text: String): String {
        var output: String = ""
        var token: String = ""
        var first = true

        for (c in text) {
            if (c in 'A'..'F' || c in 'a'..'f' || c in '0'..'9') {
                if (first) {
                    token += c
                    first = false
                }
                else {
                    token += c
                    first = true
                    var ascii = token.toInt(16)
                    output += ascii.toChar().toString()
                    token = ""
                }
            }
        }

        return output
    }

    fun processImage(bitmap: Bitmap, editText: EditText) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val scanner = BarcodeScanning.getClient()
        val recognizer = TextRecognition.getClient()

        val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0)
                        editText.text = Editable.Factory.getInstance().newEditable(barcodes[0].rawValue)
                    else {
                        editText.text = Editable.Factory.getInstance().newEditable("No text found")
                        val textResult = recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                editText.text = Editable.Factory.getInstance().newEditable(visionText.text)
                                if (visionText.text == "")
                                    editText.text = Editable.Factory.getInstance().newEditable("No text found")
                            }
                            .addOnFailureListener {
                                editText.text = Editable.Factory.getInstance().newEditable("Could not scan image")
                            }
                    }
                }
                .addOnFailureListener {
                    editText.text = Editable.Factory.getInstance().newEditable("Could not read barcode")
                }
    }
}