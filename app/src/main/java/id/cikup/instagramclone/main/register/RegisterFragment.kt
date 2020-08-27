package id.cikup.instagramclone.main.register

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import cookode.instagram_clone.R
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        btn_register.setOnClickListener(this)
        btn_signin_link.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> {
                createAccount()
            }
            R.id.btn_signin_link -> {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun createAccount() {
        val fullname = fullname_register.text.toString()
        val username = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.text.toString()

        when {
            TextUtils.isEmpty(fullname) ->
                Toast.makeText(context, "Fullname tidak boleh kosong", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username) ->
                Toast.makeText(context, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email) ->
                Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()

            else -> {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
//                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        saveInfoUser(email, fullname, username)
                    } else {
                        auth.signOut()
                        Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun saveInfoUser(email: String, fullName: String, userName: String) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey Iam student at IDN Boarding School"
        //create default image profile
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instagram-app-256b6.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=ecebab92-ce4f-463c-a16a-a81fc34b0772"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Account sudah dibuat", Toast.LENGTH_LONG).show()

                    //step 16 get post
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserID)
                        .child("Following").child(currentUserID)
                        .setValue(true)

//                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                    finish()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
//                    progressDialog.dismiss()
                }
            }
    }
}