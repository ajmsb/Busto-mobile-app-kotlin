package com.example.busto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class userSettings : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var busCard: EditText
    private lateinit var warnings: TextView
    private lateinit var database: DatabaseReference
    private var currentUser: FirebaseUser? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var originalBusCard : String
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)

        email = findViewById(R.id.emailWrite)
        oldPassword = findViewById(R.id.oldPasswordWrite)
        newPassword = findViewById(R.id.newPasswordWrite)
        confirmPassword = findViewById(R.id.passwordConfWrite)
        busCard = findViewById(R.id.cardIDTextInput)
        warnings = findViewById(R.id.youHaveXWarnings)
        database = Firebase.database.reference
        auth = Firebase.auth
        currentUser = auth.currentUser
        key =""

       val ref = database.child("users").orderByChild("uid").equalTo(currentUser?.uid)
                val valueEventListener = object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            key = ds.key.toString()
                            val dbBusCard = ds.child("buscard").getValue(String::class.java).toString()
                            busCard.setText(dbBusCard)
                            originalBusCard = dbBusCard
                            val dbWarnings = ds.child("warnings").getValue(Int::class.java)
                            if (dbWarnings == 0) {
                                warnings.setText("Good job, no warnings")
                            } else if (dbWarnings == 1) {
                                warnings.setText("You have 1 warning")
                            } else {
                                warnings.setText("You have " + dbWarnings.toString() + " warnings")
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
        ref.addListenerForSingleValueEvent(valueEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu)
        var searchItem = menu?.findItem(R.id.search)
        var searchView = searchItem?.actionView as SearchView

        //getting rid of the magnifying glass
        val magId: Int = resources.getIdentifier("android:id/search_mag_icon", null, null)
        val magImage: ImageView = searchView!!.findViewById(magId)
        val searchViewGroup: ViewGroup = magImage.getParent() as ViewGroup
        searchViewGroup.removeView(magImage)

        searchView.queryHint = "Search..."
        searchView.isIconifiedByDefault = false

        val queryTextListener = object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val intent = Intent (this@userSettings, Search::class.java).apply {
                    putExtra("query", query)
                }
                startActivity(intent)
                return true
            }
        }
        searchView.setOnQueryTextListener(queryTextListener)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId)  {
        R.id.map -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.login_icon -> {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.bus_stops -> {
            val intent = Intent(this, StopsList::class.java)
            startActivity(intent)
            true
        }
        R.id.Logout -> {
            Firebase.auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            true
        }else -> {
            super.onOptionsItemSelected(item)
        }
    }


    fun saveChanges(view: View) {
        val email = email.text.toString()
        val oldPassword = oldPassword.text.toString()
        val newPassword = newPassword.text.toString()
        val confirmPassword = confirmPassword.text.toString()
        val busCard = busCard.text.toString()

        val remail:EditText = findViewById(R.id.emailWrite)
        val roldPassword:EditText = findViewById(R.id.oldPasswordWrite)
        val rnewPassword:EditText = findViewById(R.id.newPasswordWrite)
        val rconfirmPassword:EditText = findViewById(R.id.passwordConfWrite)
        val rbuscard:EditText = findViewById(R.id.cardIDTextInput)

        if (newPassword != confirmPassword) {
            rconfirmPassword.error= "Passwords are not matching"
            rconfirmPassword.requestFocus()
        } else if (email.isNotEmpty() && oldPassword.isNotEmpty() || newPassword.isNotEmpty() || confirmPassword.isNotEmpty() && busCard.isNotEmpty()) {
            auth.signInWithEmailAndPassword(currentUser?.email, oldPassword).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    auth.currentUser!!.updateEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (busCard != originalBusCard){
                                database.child("users").child(key).child("buscard").setValue(busCard)
                            }
                            if(newPassword.isNotEmpty() && confirmPassword.isNotEmpty()) {
                                auth.currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this, userSettings::class.java)
                                        startActivity(intent)
                                        Toast.makeText(this, "Information updated succesfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        rnewPassword.error= "New password must have at least 6 characters"
                                        rnewPassword.requestFocus()
                                    }
                                }
                            } else {
                                val intent = Intent(this, userSettings::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Information updated succesfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            remail.error= "Please type correct email"
                            remail.requestFocus()
                        }
                    }
                }
                else {
                    roldPassword.error= "Please type correct Password"
                    roldPassword.requestFocus()
                }
            }
        } else {
            Toast.makeText(this, "Email, old Password and buscard are required", Toast.LENGTH_LONG).show()
        }
    }
    fun deleteAccount(view: View) {
        val roldPassword:EditText = findViewById(R.id.oldPasswordWrite)
        val oldPassword = oldPassword.text.toString()
        if(oldPassword.isEmpty()) {
            roldPassword.error= "Please input current password"
            roldPassword.requestFocus()
        } else {
        val builder = AlertDialog.Builder(this@userSettings)
        builder.setMessage("Do you really want to delete your account?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    auth.signInWithEmailAndPassword(currentUser?.email, oldPassword).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            deleteUserDb()
                            auth.currentUser!!.delete().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    Toast.makeText(this, "Account deleted succesfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            roldPassword.error= "Wrong password"
                            roldPassword.requestFocus()
                        }
                    }
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
        val alert = builder.create()
        alert.show()
        }
    }

    private fun deleteUserDb() {
        if (key != "") {
           val ref = database.child("users")
            val listener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userInDb = snapshot.child(key).exists()
                    if (userInDb) {
                        database.child("users").child(key).removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@userSettings, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
            ref.addListenerForSingleValueEvent(listener)
        }
    }

    override fun onStart() {
        currentUser = auth.currentUser
        if(currentUser == null) {
            Toast.makeText(this@userSettings, "Please login first", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        email.setText(currentUser?.email)
        super.onStart()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}