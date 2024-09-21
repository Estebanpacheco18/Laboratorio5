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
package com.example.cupcake.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cupcake.R
import com.example.cupcake.data.OrderUiState
import com.example.cupcake.ui.components.FormattedPriceLabel
import com.example.cupcake.ui.theme.CupcakeTheme

/**
 * This composable expects [orderUiState] that represents the order state, [onCancelButtonClicked]
 * lambda that triggers canceling the order and passes the final order to [onSendButtonClicked]
 * lambda
 */
@Composable
fun OrderSummaryScreen(
    orderUiState: OrderUiState,
    onSendButtonClicked: (String, String) -> Unit,
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val orderDetails = "Order details:\n" +
            "Quantity: ${orderUiState.quantity}\n" +
            "Flavor: ${orderUiState.flavor}\n" +
            "Pickup Date: ${orderUiState.date}\n" +
            "Price: ${orderUiState.price}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.order_summary),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_small))
            )
            Text(
                text = stringResource(R.string.quantity, orderUiState.quantity),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.flavor, orderUiState.flavor),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.pickup_date, orderUiState.date),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            FormattedPriceLabel(
                subtotal = orderUiState.price,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { shareOrder(context, orderDetails) }  // Llamada a la función shareOrder
                ) {
                    Text(stringResource(R.string.send))
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCancelButtonClicked
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

fun onSendButtonClicked() {
    TODO("Not yet implemented")
}

fun shareOrder(context: Context, orderDetails: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, orderDetails)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@Preview(showBackground=true)
@Composable
fun OrderSummaryPreview() {
    CupcakeTheme {
        OrderSummaryScreen(
            orderUiState = OrderUiState(0, "Test", "Test", "$300.00"),
            onCancelButtonClicked = {},
            onSendButtonClicked = { _: String, _: String -> },
            modifier = Modifier.fillMaxHeight()

        )
    }
}
