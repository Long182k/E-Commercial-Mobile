package com.example.e_commercial.ui.feature.account.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_commercial.R
import com.example.e_commercial.navigation.HomeScreen
import com.example.e_commercial.navigation.RegisterScreen
import org.koin.androidx.compose.koinViewModel


// Define the purple color to match the design
val PurpleButton = Color(0xFF6B4EFF)

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = koinViewModel()) {
    val loginState = viewModel.loginState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = loginState.value) {
            is LoginState.Success -> {
                LaunchedEffect(loginState.value) {
                    navController.navigate(HomeScreen) {
                        popUpTo(HomeScreen) {
                            inclusive = true
                        }
                    }
                }
            }
            is LoginState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
                LoginContent(
                    onSignInClicked = { email, password ->
                        viewModel.login(email, password)
                    },
                    onRegisterClick = {
                        navController.navigate(RegisterScreen)
                    }
                )
            }
            is LoginState.Loading -> {
                CircularProgressIndicator()
                Text(text = stringResource(id = R.string.loading))
            }
            else -> {
                LoginContent(
                    onSignInClicked = { email, password ->
                        viewModel.login(email, password)
                    },
                    onRegisterClick = {
                        navController.navigate(RegisterScreen)
                    }
                )
            }
        }
    }
}

@Composable
fun LoginContent(onSignInClicked: (String, String) -> Unit, onRegisterClick: () -> Unit) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.email)) }
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { onSignInClicked(email.value, password.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = email.value.isNotEmpty() && password.value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleButton,
                contentColor = Color.White,
                disabledContainerColor = PurpleButton.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = stringResource(id = R.string.login),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.dont_have_account),
            modifier = Modifier
                .padding(8.dp)
                .clickable { onRegisterClick() }
        )
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewLoginScreen() {
    LoginContent(onSignInClicked = { email, password ->
    }, onRegisterClick = {})
}