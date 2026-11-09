package com.cursokotlin.petly.presentations.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cursokotlin.petly.R
import com.cursokotlin.petly.presentations.viewmodels.MascotaItemUi
import com.cursokotlin.petly.ui.theme.PetlyBorder
import com.cursokotlin.petly.ui.theme.PetlyChipNeutral
import com.cursokotlin.petly.ui.theme.PetlyChipVaccinated
import com.cursokotlin.petly.ui.theme.PetlyGreenText
import com.cursokotlin.petly.ui.theme.PetlyOrange
import com.cursokotlin.petly.ui.theme.PetlyOrangeChip
import com.cursokotlin.petly.ui.theme.PetlyRedChip
import com.cursokotlin.petly.ui.theme.PetlyRedText
import com.cursokotlin.petly.ui.theme.PetlyTextSecondary
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MascotaCard(
    mascota: MascotaItemUi,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forma = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .clickable(onClick = onClick),
        shape = forma,
        color = Color.White,
        border = BorderStroke(1.dp, PetlyBorder),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MascotaAvatar(especie = mascota.especie)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mascota.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${mascota.especie} · " +
                                pluralStringResource(R.plurals.age_years, mascota.edadAnios, mascota.edadAnios),
                        style = MaterialTheme.typography.bodySmall,
                        color = PetlyTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onEliminarClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.action_delete),
                        modifier = Modifier.size(24.dp)
                    )
                }
                PetlyIcon(id = R.drawable.ic_chevron_right)
            }
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                mascota.comidaResumen?.let {
                    PetlyChip(
                        text = it,
                        background = PetlyChipNeutral,
                        contentColor = PetlyTextSecondary,
                        iconRes = R.drawable.ic_food,
                    )
                }
                if (!mascota.yaComio) {
                    PetlyChip(
                        text = stringResource(R.string.feeding_pending_today),
                        background = PetlyOrangeChip,
                        contentColor = PetlyOrange,
                        iconRes = R.drawable.ic_warning,
                    )
                }
                if (mascota.estaVacunado) {
                    PetlyChip(
                        text = stringResource(R.string.chip_vaccinated),
                        background = PetlyChipVaccinated,
                        contentColor = PetlyGreenText,
                        iconRes = R.drawable.ic_check,
                    )
                } else {
                    PetlyChip(
                        text = stringResource(R.string.chip_not_vaccinated),
                        background = PetlyRedChip,
                        contentColor = PetlyRedText,
                    )
                }
                if (mascota.yaComio) {
                    PetlyChip(
                        text = stringResource(R.string.chip_fed),
                        background = PetlyChipVaccinated,
                        contentColor = PetlyGreenText,
                        iconRes = R.drawable.ic_check,
                    )
                } else {
                    PetlyChip(
                        text = stringResource(R.string.chip_not_fed),
                        background = PetlyRedChip,
                        contentColor = PetlyRedText,
                    )
                }
            }
        }
    }
}