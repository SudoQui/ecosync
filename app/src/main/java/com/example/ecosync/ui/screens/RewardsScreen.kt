package com.example.ecosync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecosync.viewmodel.StepViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RewardsScreen(vm: StepViewModel = StepViewModel.shared) {
    val ui by vm.ui.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Rewards", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Earn points by walking then redeem",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(12.dp))
        Text("Your points ${ui.points}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Section(title = "Bus pass") {
            ui.rewards.filter { it.name.contains("Bus", ignoreCase = true) }.forEach { r ->
                RewardRow(vm, r.id, r.name, r.partner, r.pointsRequired, r.redeemedAt, r.code)
            }
        }

        Section(title = "Plant one tree") {
            ui.rewards.filter { it.name.contains("tree", ignoreCase = true) }.forEach { r ->
                RewardRow(vm, r.id, r.name, r.partner, r.pointsRequired, r.redeemedAt, r.code)
            }
        }

        Section(title = "Oscars Cafe") {
            ui.rewards.filter {
                !it.name.contains("Bus", true) && !it.name.contains("tree", true)
            }.forEach { r ->
                RewardRow(vm, r.id, r.name, r.partner, r.pointsRequired, r.redeemedAt, r.code)
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Column(content = content)
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun RewardRow(
    vm: StepViewModel,
    id: String,
    name: String,
    partner: String,
    pointsRequired: Int,
    redeemedAt: String?,
    code: String?
) {
    val stepsRemaining = vm.stepsNeededForReward(pointsRequired)
    val canRedeem = vm.canRedeem(pointsRequired)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text("$partner • $pointsRequired pts")
            Spacer(Modifier.height(8.dp))

            if (redeemedAt == null) {
                Button(
                    onClick = { vm.redeem(id) },
                    enabled = canRedeem,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (canRedeem) {
                        Text("Redeem")
                    } else {
                        Text("Need $stepsRemaining steps")
                    }
                }
            } else {
                Text(
                    "Redeemed at ${formatMillis(redeemedAt)}",
                    color = MaterialTheme.colorScheme.primary
                )
                if (!code.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text("Show to vendor code: $code", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

private fun formatMillis(millisString: String): String {
    val millis = millisString.toLongOrNull() ?: return millisString
    val fmt = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault())
    return fmt.format(Date(millis))
}