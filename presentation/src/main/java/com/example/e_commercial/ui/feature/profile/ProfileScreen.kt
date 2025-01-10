package com.example.e_commercial.ui.feature.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.e_commercial.EcommercialSession
import com.example.e_commercial.R
import com.example.e_commercial.navigation.LoginScreen

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    isDarkTheme: MutableState<Boolean>
) {
    val user by viewModel.user.observeAsState()
    val context = LocalContext.current
    val isDialogVisible = remember { mutableStateOf(false) }
    val isAvatarDialogVisible = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val changePasswordState by viewModel.changePasswordState.observeAsState()

    // Observe password change state and show success or error messages
    changePasswordState?.let { result ->
        result.onSuccess {
            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetChangePasswordState() // Reset the state after showing success
        }
        result.onFailure { exception ->
            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            viewModel.resetChangePasswordState() // Reset the state after showing error
        }
    }

    var editedName by remember { mutableStateOf("") }
    var isEditingName by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dark Mode Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (isDarkTheme.value) "Dark Mode" else "Light Mode",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme.value,
                onCheckedChange = { isChecked ->
                    isDarkTheme.value = isChecked
                    val session = EcommercialSession(context)
                    session.saveTheme(isChecked)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = if (isDarkTheme.value) Color.Black else Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { isAvatarDialogVisible.value = true }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(24.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Avatar Dialog
        if (isAvatarDialogVisible.value) {
            AvatarSelectionDialog(
                onDismiss = { isAvatarDialogVisible.value = false },
                onImageSelected = { newAvatarUri ->
                    isAvatarDialogVisible.value = false
                    viewModel.updateUserAvatar(newAvatarUri)
                    Toast.makeText(context, "Avatar updated successfully!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // User Details Section
        user?.let { currentUser ->
            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyLarge.copy()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name Edit Section
        if (isEditingName) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Edit Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        isEditingName = false
                        viewModel.updateUserName(editedName)
                        Toast.makeText(context, "Name updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Save")
                }
                Button(onClick = { isEditingName = false }) {
                    Text("Cancel")
                }
            }
        } else {
            Button(
                onClick = {
                    isEditingName = true
                    editedName = user?.name.orEmpty()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Edit Profile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Change Password Button
        Button(
            onClick = { isDialogVisible.value = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Change Password")
        }

        // Change Password Dialog
        if (isDialogVisible.value) {
            user?.let { currentUser ->
                ChangePasswordDialog(
                    isVisible = isDialogVisible,
                    defaultEmail = currentUser.email,
                    onSubmit = { email, oldPassword, newPassword ->
                        viewModel.changePassword(email, oldPassword, newPassword)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text(text = "Log Out")
        }
    }
}

@Composable
fun ChangePasswordDialog(
    isVisible: MutableState<Boolean>,
    defaultEmail: String,
    onSubmit: (String, String, String) -> Unit
) {
    if (isVisible.value) {
        val focusManager = LocalFocusManager.current
        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        // Validation function
        fun validateInputs(): Boolean {
            return when {
                oldPassword.isEmpty() -> {
                    errorMessage = "Old password cannot be empty."
                    false
                }
                newPassword.isEmpty() -> {
                    errorMessage = "New password cannot be empty."
                    false
                }
                newPassword.length < 8 -> {
                    errorMessage = "New password must be at least 8 characters long."
                    false
                }
                oldPassword == newPassword -> {
                    errorMessage = "New password must be different from the old password."
                    false
                }
                else -> {
                    errorMessage = ""
                    true
                }
            }
        }

        AlertDialog(
            onDismissRequest = {
                focusManager.clearFocus()
                isVisible.value = false
            },
            title = { Text(text = "Change Password") },
            text = {
                Column {
                    // Email Field (read-only)
                    OutlinedTextField(
                        value = defaultEmail,
                        onValueChange = {},
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Old Password Field
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Old Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // New Password Field
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Error Message (if any)
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    focusManager.clearFocus()
                    if (validateInputs()) {
                        onSubmit(defaultEmail, oldPassword, newPassword)
                        isVisible.value = false
                    }
                }) {
                    Text("Change Password")
                }
            },
            dismissButton = {
                Button(onClick = {
                    focusManager.clearFocus()
                    isVisible.value = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AvatarSelectionDialog(
    onDismiss: () -> Unit,
    onImageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Avatar") },
        text = { Text("Select a new avatar from your gallery or take a photo.") },
        confirmButton = {
            Button(onClick = {
                val newImageUri = "dummy_image_uri"
                onImageSelected(newImageUri)
            }) {
                Text("Select Image")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
