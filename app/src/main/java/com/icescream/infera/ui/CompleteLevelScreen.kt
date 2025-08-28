package com.icescream.infera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.icescream.infera.R
import androidx.compose.foundation.Image


@Composable
fun CompleteLevelScreen(
    onContinue: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF89EC77)) // Verde claro
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 32.dp, start = 30.dp, end = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "¡Increíble!",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 27.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
            // Imagen central
            Image(
                painter = painterResource(id = R.drawable.complete_lession),
                contentDescription = "Animalito feliz",
                modifier = Modifier
                    .size(220.dp)
                    .padding(vertical = 6.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Completaste la lección y me ayudaste a aprender más.",
                    color = Color.White,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
                Text(
                    text = "¿Listo para Continuar?",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE158), // amarillo del mockup
                    contentColor = Color(0xFF3B3800)
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    "Continuar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            }
        }
    }
}
