package com.chirilglance.androidglancedna.presentation.examples.auth.emailscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chirilglance.androidglancedna.core.domain.model.UiState
import com.chirilglance.androidglancedna.core.ui.components.ClubConnectCardDefaults
import com.chirilglance.androidglancedna.core.ui.components.DefaultCard
import com.chirilglance.androidglancedna.core.ui.components.buttons.PrimaryButton

@Composable
fun EmailSignInScreen(
    onSuccess: () -> Unit,
    viewModel: EmailSignInViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val uiState by viewModel.uiState.collectAsState()
    val isRegisterMode = viewModel.isRegisterMode

    var passwordVisible by remember { mutableStateOf(false) }

    // Handle successful authentication
    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            onSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            DefaultCard(
                modifier = Modifier.fillMaxWidth(),
                titleContent = ClubConnectCardDefaults.Title(
                    if (isRegisterMode) "Create Account" else "Welcome Back"
                ),
                subtitleContent = ClubConnectCardDefaults.Subtitle(
                    if (isRegisterMode) "Register with Email" else "Sign in with Email"
                ),
                descriptionContent = ClubConnectCardDefaults.Description(
                    if (isRegisterMode)
                        "Enter your email and create a secure password"
                    else
                        "Enter your credentials to continue"
                ),
                actions = {
                    Column {
                        // Email field
                        OutlinedTextField(
                            value = viewModel.email,
                            onValueChange = viewModel::updateEmail,
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true,
                            isError = !viewModel.validateEmail().isValid && viewModel.email.isNotEmpty(),
                            supportingText = {
                                if (!viewModel.validateEmail().isValid && viewModel.email.isNotEmpty()) {
                                    Text(
                                        text = viewModel.validateEmail().errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Password field
                        OutlinedTextField(
                            value = viewModel.password,
                            onValueChange = viewModel::updatePassword,
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible)
                                            Icons.Default.Email
                                        else
                                            Icons.Default.Lock,
                                        contentDescription = if (passwordVisible)
                                            "Hide password"
                                        else
                                            "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    if (viewModel.canSubmit()) {
                                        viewModel.submitEmailAuth { onSuccess() }
                                    }
                                }
                            ),
                            singleLine = true,
                            isError = !viewModel.validatePassword().isValid && viewModel.password.isNotEmpty(),
                            supportingText = {
                                if (!viewModel.validatePassword().isValid && viewModel.password.isNotEmpty()) {
                                    Text(
                                        text = viewModel.validatePassword().errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // API error message
                        if (uiState is UiState.Error) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = (uiState as UiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Forgot password (only in sign-in mode)
                        if (!isRegisterMode) {
                            TextButton(
                                onClick = { viewModel.sendPasswordReset() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Forgot Password?")
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            PrimaryButton(
                text = if (isRegisterMode) "Create Account" else "Sign In",
                onClick = {
                    keyboardController?.hide()
                    viewModel.submitEmailAuth { onSuccess() }
                },
                enabled = viewModel.canSubmit(),
                isLoading = uiState is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle between sign in and register
            TextButton(
                onClick = { viewModel.toggleMode() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    if (isRegisterMode)
                        "Already have an account? Sign In"
                    else
                        "Don't have an account? Register"
                )
            }
        }
    }
}