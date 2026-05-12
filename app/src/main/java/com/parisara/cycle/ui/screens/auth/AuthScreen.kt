package com.parisara.cycle.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.parisara.cycle.ui.theme.GreenPrimary

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUp        by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        // App Icon Card
        Card(
            modifier = Modifier.size(80.dp),
            colors   = CardDefaults.cardColors(
                containerColor = GreenPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.DirectionsBike,
                    contentDescription = "App Logo",
                    tint               = Color.White,
                    modifier           = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text       = "Parisara-Cycle",
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color      = GreenPrimary
        )

        Text(
            text  = "Green Commuter Guide 🚴",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Sign In / Sign Up Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Sign In Button
            Button(
                onClick  = { isSignUp = false },
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (!isSignUp)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart    = 12.dp,
                    bottomStart = 12.dp,
                    topEnd      = 0.dp,
                    bottomEnd   = 0.dp
                )
            ) {
                Text(
                    text  = "Sign In",
                    color = if (!isSignUp)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sign Up Button
            Button(
                onClick  = { isSignUp = true },
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (isSignUp)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(
                    topStart    = 0.dp,
                    bottomStart = 0.dp,
                    topEnd      = 12.dp,
                    bottomEnd   = 12.dp
                )
            ) {
                Text(
                    text  = "Sign Up",
                    color = if (isSignUp)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value         = email,
            onValueChange = { email = it },
            label         = { Text("Email") },
            leadingIcon   = {
                Icon(
                    imageVector        = Icons.Default.Email,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = ImeAction.Next
            ),
            singleLine = true,
            modifier   = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Field
        OutlinedTextField(
            value         = password,
            onValueChange = { password = it },
            label         = { Text("Password") },
            leadingIcon   = {
                Icon(
                    imageVector        = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Done
            ),
            singleLine = true,
            modifier   = Modifier.fillMaxWidth()
        )

        // Error Message
        AnimatedVisibility(visible = authState is AuthState.Error) {
            val errorMsg = (authState as? AuthState.Error)?.message ?: ""
            Card(
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text     = errorMsg,
                    color    = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style    = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Action Button
        Button(
            onClick = {
                if (isSignUp) {
                    viewModel.createAccountWithEmail(email, password)
                } else {
                    viewModel.signInWithEmail(email, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = email.isNotBlank()
                    && password.length >= 6
                    && authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    color       = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text     = if (isSignUp) "Create Account" else "Sign In",
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Test Account Card
        Card(
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text       = "💡 Test Account",
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text  = "Email:    test@parisara.com",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text  = "Password: Test@1234",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick  = {
                        email    = "test@parisara.com"
                        password = "Test@1234"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Fill Test Credentials")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}