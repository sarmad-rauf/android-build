package com.es.marocapp.security

import com.es.marocapp.utils.RootValues

object EncryptionUtils{
    //using above code these key and iv was generated
    val hexKey = RootValues.getInstance().hexKeyAesGcm

    val hexIV = RootValues.getInstance().hexIVAesGcm

    val hexKeyAESCBC =RootValues.getInstance().hexKeyAesCBC

    val hexIVAESCBC = RootValues.getInstance().hexIVAesCBC
    public fun encryptString(value:String):String{
        //encrypt - result base64 encoded string
        val encryptedText =
            AesGcm256.encrypt(value, AesGcm256.HexToByte(hexKey), AesGcm256.HexToByte(hexIV))

        return  encryptedText
    }

    public fun decryptString(value:String):String{
        //decrypt - result plain string
        val decryptedText = AesGcm256.decrypt(
            value,
            AesGcm256.HexToByte(hexKey),
            AesGcm256.HexToByte(hexIV)
        )

        return  decryptedText
    }

    public fun encryptStringAESCBC(value:String):String{
        //encrypt - result base64 encoded string
        val encryptedText =
            CryptoHandler.getInstance().encrypt(value, AesGcm256.HexToByte(hexKeyAESCBC), AesGcm256.HexToByte(
                hexIVAESCBC))

        return  encryptedText
    }

    public fun decryptStringAESCBC(value:String):String{
        //decrypt - result plain string
        val decryptedText = CryptoHandler.getInstance().decrypt(
            value,
            AesGcm256.HexToByte(hexKeyAESCBC),
            AesGcm256.HexToByte(hexIVAESCBC)
        )

        return  decryptedText
    }

    fun ecryptionDecryptionTest(){
        //Generate and dump KEY so we could use again
        //System.out.println(AesGcm256.toHex(AesGcm256.NewKey()));

        //Generate and dump IV so we could use again
        //System.out.println(AesGcm256.toHex(AesGcm256.NewIv()));

        //Console.ReadKey();

        //using above code these key and iv was generated

        //Generate and dump KEY so we could use again
        //System.out.println(AesGcm256.toHex(AesGcm256.NewKey()));

        //Generate and dump IV so we could use again
        //System.out.println(AesGcm256.toHex(AesGcm256.NewIv()));

        //Console.ReadKey();


        val plainText = "Test encryption and decryption"
        println("Plain Text: $plainText")

        //encrypt - result base64 encoded string

        //encrypt - result base64 encoded string
        val encryptedText =
            AesGcm256.encrypt(plainText, AesGcm256.HexToByte(hexKey), AesGcm256.HexToByte(hexIV))
        println("Encrypted base64 encoded: $encryptedText")

        //decrypt - result plain string

        //decrypt - result plain string
        val decryptedText = AesGcm256.decrypt(
            encryptedText,
            AesGcm256.HexToByte(hexKey),
            AesGcm256.HexToByte(hexIV)
        )
        println("Decrypted Text: $decryptedText")

        if (plainText == decryptedText) {
            println("Test Passed")
        } else {
            println("Test Failed")
        }

        /* Console Output
            Plain Text: Test encryption and decryption
            Encrypted base64 encoded:
                   A/boAixWJKflKviHp2cfDl6l/xn1qw2MsHcKFkrOfm2XOVmawIFct4fS1w7wKw==
            Decrypted Text: Test encryption and decryption
            Test Passed
            Press any key to continue . . .
            */
    }
}