package com.example.fse_project.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fse_project.presentation.navigation.Screen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(navController: NavController,viewModel: LoginViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()
    var showLoginDialog by remember { mutableStateOf(false) }
    var showSignUpDialog by remember { mutableStateOf(false) }
    var error = state.error
    val context = LocalContext.current
    val isLoginSuccessful = state.isLoginSuccessful

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when(it){
                is LoginEffect.ShowToast -> Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isLoginSuccessful) {
        if (isLoginSuccessful){
            navController.navigate("main_graph") {
                //viewModel.setUserId()
                popUpTo("auth") { inclusive = true }
            }
        }
    }
    LaunchedEffect(error) {
        if (error.isNotBlank()){
            Toast.makeText(context,error,Toast.LENGTH_SHORT).show()
        }
    }

    if(showLoginDialog){
        LoginAlertDialog(
            onDismiss = {showLoginDialog = false},
            viewModel = viewModel
        )
    }

    if(showSignUpDialog){
        SignUpAlertDialog(
            onDismiss = {showSignUpDialog = false},
            viewModel = viewModel
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                showLoginDialog = true
            }) {
                Text(text = "Login",fontSize = 16.sp)
            }

            Button(onClick = {
                showSignUpDialog = true
            }) {
                Text(text = "Sign Up",fontSize = 16.sp)
            }
        }
    }

}

@Composable
fun LoginAlertDialog(onDismiss : () -> Unit,viewModel: LoginViewModel) {

    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = {
            Text(text = "Login",fontSize = 16.sp)
        },
        text = {
            Column {
                TextField(value = emailText, onValueChange = {emailText = it }, placeholder = {Text("email", color = Color.LightGray)})
                TextField(value = passwordText, onValueChange = {passwordText = it }, placeholder = {Text("password", color = Color.LightGray)})
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.signIn(emailText,passwordText)
            }) {
                Text(text = "Login", fontSize = 16.sp)
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel", fontSize = 16.sp)
            }
        }
    )
}


@Composable
fun SignUpAlertDialog(onDismiss : () -> Unit,viewModel: LoginViewModel) {

    var nameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = {
            Text(text = "Sign Up",fontSize = 16.sp)
        },
        text = {
            Column {
                OutlinedTextField(value = nameText, onValueChange = {nameText = it }, placeholder = {Text("name", color = Color.LightGray)})
                OutlinedTextField(value = emailText, onValueChange = {emailText = it }, placeholder = {Text("email", color = Color.LightGray)})
                OutlinedTextField(value = passwordText, onValueChange = {passwordText = it }, placeholder = {Text("password", color = Color.LightGray)})
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.signUp(nameText,emailText,passwordText)
            }) {
                Text(text = "Sign Up", fontSize = 16.sp)
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel", fontSize = 16.sp)
            }
        }
    )
}