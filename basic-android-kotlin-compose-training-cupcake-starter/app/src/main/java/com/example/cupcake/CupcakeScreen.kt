/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.data.DataSource
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */

enum class CupcakeScreen() {
    Start,
    Flavor,
    Pickup,
    Summary
}

@Composable
fun CupcakeAppBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    Scaffold(
        topBar = {
            CupcakeAppBar(
                canNavigateBack = false,
                navigateUp = { /* TODO: implement back navigation */ }
            )

        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
// En la pantalla de inicio, configuramos la navegación hacia la pantalla de selección de sabor
            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = {
                        viewModel.setQuantity(it) // Actualizamos la cantidad en el ViewModel
                        navController.navigate(CupcakeScreen.Flavor.name) // Navegamos a la pantalla de selección de sabor
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))
                )
            }

// En la pantalla de selección de sabor, configuramos la navegación hacia la pantalla de selección de fecha y la cancelación del pedido
            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name) // Navegamos a la pantalla de selección de fecha
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        ) // Cancelamos el pedido y volvemos a la pantalla de inicio
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChanged = { viewModel.setFlavor(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }

// En la pantalla de selección de fecha, configuramos la navegación hacia la pantalla de resumen y la cancelación del pedido
            composable(route = CupcakeScreen.Pickup.name) {
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name) // Navegamos a la pantalla de resumen
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        ) // Cancelamos el pedido y volvemos a la pantalla de inicio
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { viewModel.setDate(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }

// En la pantalla de resumen, solo configuramos la cancelación del pedido
            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        ) // Cancelamos el pedido y volvemos a la pantalla de inicio
                    },
                    onSendButtonClicked = { _: String, _: String -> Int},
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

// Función para cancelar el pedido y navegar de regreso a la pantalla de inicio
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,  // ViewModel para manejar el estado de la orden
    navController: NavHostController // Controlador de navegación para gestionar la pila de actividades
) {
    viewModel.resetOrder() // Resetea el estado de la orden en el ViewModel
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false) // Navega de regreso a la pantalla de inicio
}
