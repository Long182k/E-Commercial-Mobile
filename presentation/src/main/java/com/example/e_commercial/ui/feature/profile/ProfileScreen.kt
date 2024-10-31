package com.example.e_commercial.ui.feature.profile

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.e_commercial.EcommercialSession
import com.example.e_commercial.R
import com.example.e_commercial.navigation.LoginScreen

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val user by viewModel.user.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(Color.White)
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

        // Name Section
        user?.let { currentUser ->
            Text(
                text = currentUser.name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Email Section
        user?.let { currentUser ->
            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Gray
                )
            )
        }

//        Spacer(modifier = Modifier.height(32.dp))

        // Profile Menu Items
//        ProfileMenuItem(
//            icon = R.drawable.ic_profile,
//            title = "Edit Profile",
//            onClick = { /* Handle edit profile */ }
//        )

//        ProfileMenuItem(
//            icon = R.drawable.ic_settings,
//            title = "Settings",
//            onClick = { /* Handle settings */ }
//        )
//
//        ProfileMenuItem(
//            icon = R.drawable.ic_help,
//            title = "Help Center",
//            onClick = { /* Handle help center */ }
//        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                EcommercialSession.clearUser()
                navController.navigate(LoginScreen) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = "Log Out")
        }
    }
}

//@Composable
//fun ProfileMenuItem(
//    @DrawableRes icon: Int,
//    title: String,
//    onClick: () -> Unit
//) {
//    Surface(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(vertical = 8.dp),
//        color = Color.Transparent
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = icon),
//                contentDescription = null,
//                modifier = Modifier.size(24.dp),
//                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
//            )
//
//            Spacer(modifier = Modifier.width(16.dp))
//
//            Text(
//                text = title,
//                style = MaterialTheme.typography.bodyLarge,
//                modifier = Modifier.weight(1f)
//            )
//
////            Icon(
////                painter = painterResource(id = R.drawable.ic_arrow_right),
////                contentDescription = null,
////                tint = Color.Gray,
////                modifier = Modifier.size(24.dp)
////            )
//        }
//    }
//}