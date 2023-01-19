package com.example.busto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var edemail: EditText
    private lateinit var edpass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        edemail = findViewById(R.id.editTextTextEmailAddress)
        edpass = findViewById(R.id.editTextTextPassword)
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
                val intent = Intent (this@LoginActivity, Search::class.java).apply {
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
        R.id.settings -> {
            val intent = Intent (this, userSettings::class.java)
            startActivity(intent)
            true
        }
        R.id.map -> {
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.bus_stops -> {
            val intent = Intent (this, StopsList::class.java)
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

    fun login(view: View) {
        val email = edemail.text.toString()
        val password = edpass.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        val id = user.uid
                        // user.uid for currently logged in users id
                        val intent = Intent(this,MainActivity::class.java).apply {
                            putExtra("UID", id)
                        }

                        startActivity(intent)
                    }else{
                        val toast = Toast.makeText(this, "Invalid Credentials",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
                    }
                }

        }else{
            val toast = Toast.makeText(this, "Please type in your email and password",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER,0,0)
            toast.show()
        }
    }

    fun toRegister(view: View) {
        val intent = Intent(this, RegisterActivity::class.java).apply {
        }
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser);
        }
    }


    fun updateUI (currentUser: FirebaseUser?){

    }
}