package id.cikup.instagramclone.main.login

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import cookode.instagram_clone.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment(), View.OnClickListener {

    val auth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        btn_login.setOnClickListener(this)
        btn_signup_link.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_login ->{
                loginUser()
            }
            R.id.btn_signup_link ->{
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun loginUser(){
        val email:String = email_login.text.toString()
        val password:String = password_login.text.toString()
        when{
            TextUtils.isEmpty(email) ->
                Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password) ->
                Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()

            else ->{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        // to home
                        findNavController().navigate(R.id.action_loginFragment_to_homeActivity)
                    }else{
                        auth.signOut()
                        Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
//
//    override fun onStart() {
//        if (auth.currentUser != null){
//            //to home
//            findNavController().navigate(R.id.action_loginFragment_to_homeActivity)
//        }
//        super.onStart()
//    }


}