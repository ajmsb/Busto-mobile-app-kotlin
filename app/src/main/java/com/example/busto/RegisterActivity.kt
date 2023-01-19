package com.example.busto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Initialize Firebase Auth
        auth = Firebase.auth
        database = Firebase.database.reference


        val register: Button = findViewById(R.id.btn_register)
        register.setOnClickListener {
            registerUser()
        }
    }
    private fun registerUser() {
        val email: TextView = findViewById(R.id.emailInput)
        val password: TextView = findViewById(R.id.passwordInput)
        val passwordConfirm: TextView = findViewById(R.id.PasswordConfirmationInput)
        if (email.text.toString().isEmpty()){
            email.error= "Please enter email"
            email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            email.error= "Please enter Valid email"
            email.requestFocus()
            return
        }
        if (password.text.toString().length < 6){
            password.error= "Please enter Password with at least 6 characters"
            password.requestFocus()
            return
        }

        if (passwordConfirm.text.toString().isEmpty()){
            passwordConfirm.error= "Please enter Password"
            passwordConfirm.requestFocus()
            return
        }else if(passwordConfirm.text.toString() != password.text.toString() ){
            passwordConfirm.error= "Passwords don't match"
            passwordConfirm.requestFocus()
            return
        }

        var buscardId: EditText = findViewById(R.id.cardIdInput)
        var checkbox: CheckBox = findViewById(R.id.termsAgreementCheckBox)

        if (checkbox.isChecked() && buscardId.text.toString().isNotEmpty() ){

            val remail: String = email.text.toString()
            val rpass: String = password.text.toString()

            auth.createUserWithEmailAndPassword(remail, rpass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        auth.signInWithEmailAndPassword(remail, rpass)

                        var uid = Firebase.auth.currentUser.uid

                        var pos = database.child("users").push().key ?: ""

                        database.child("users").child(pos).child("uid").setValue(uid)
                        database.child("users").child(pos).child("buscard").setValue(buscardId.text.toString())
                        database.child("users").child(pos).child("warnings").setValue(0)

                        val intent = Intent (this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            if (buscardId.text.toString().isEmpty()){
                buscardId.error= "Please enter your card id"
                buscardId.requestFocus()
            }else {
                checkbox.error= ""
                checkbox.requestFocus()
                Toast.makeText(baseContext, "Please agree to our terms and conditions", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

