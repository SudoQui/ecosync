package com.example.ecosync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecosync.viewmodel.StepViewModel

@Composable
fun CommunityScreen(vm: StepViewModel = StepViewModel.shared) {
    val ui by vm.ui.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Community", style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(onClick = { vm.setupDemoCommunities() }) {
                Text("Set up community")
            }
        }

        // Friends leaderboard
        Text("Friends leaderboard", style = MaterialTheme.typography.titleMedium)
        LeaderboardList(
            entries = ui.friends
                .sortedWith(
                    compareByDescending<StepViewModel.Friend> { it.steps }
                        .thenByDescending { it.points }
                )
                .map { Triple(it.name, it.steps, it.points) }
        )

        // Communities
        if (ui.communities.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("Communities", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ui.communities, key = { it.id }) { c ->
                    CommunityCard(
                        name = c.name,
                        members = c.members
                            .sortedWith(
                                compareByDescending<StepViewModel.Friend> { it.steps }
                                    .thenByDescending { it.points }
                            )
                    )
                }
            }
        } else {
            Text("No communities yet. Tap “Set up community” to add UC Teaching Staff and KPMG Account team.")
        }
    }
}

@Composable
private fun LeaderboardList(entries: List<Triple<String, Int, Int>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEachIndexed { i, e ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${i + 1}. ${e.first}", style = MaterialTheme.typography.titleMedium)
                        Text("%,d steps".format(e.second))
                    }
                    Text("${e.third} pts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(
    name: String,
    members: List<StepViewModel.Friend>
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Members ${members.size}")
            Spacer(Modifier.height(6.dp))
            members.take(3).forEachIndexed { idx, f ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${idx + 1}. ${f.name}")
                    Text("%,d steps  •  ${f.points} pts".format(f.steps))
                }
            }
        }
    }
}