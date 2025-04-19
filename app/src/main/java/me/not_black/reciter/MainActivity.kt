package me.not_black.reciter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.not_black.reciter.ui.theme.ReciterTheme
import me.not_black.reciter.wrapper.ReciteWrapper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        inst = this
        setContent {
            ReciterTheme {
                ReciterApp()
            }
        }
    }

    companion object {
        private lateinit var inst: MainActivity
        fun simpleToast(textId: Int) {
            Toast.makeText(inst, textId, Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReciterApp() {
    var inputText by remember { mutableStateOf("") }
    var wrapper: ReciteWrapper? by remember { mutableStateOf(null) }
    var outputText by remember { mutableStateOf("") }
    var sentence by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/plain"
    }
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            println(data)
            data?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val text = stream.bufferedReader().use { it.readText() }
                    inputText = text
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Column (horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(0.dp, 32.dp)) {
        TextButton(R.string.select_file, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            resultLauncher.launch(intent)
        }
        TextField(
            inputText,
            onValueChange = { inputText = it },
            placeholder = { Text(stringResource(R.string.input_text)) },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(0.95f)
                .height(128.dp)
        )
        TextButton(R.string.confirm, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            if (inputText.isNotEmpty())
                wrapper = ReciteWrapper(inputText)
            else
                MainActivity.simpleToast(R.string.input_empty)
        }
        FlowRow (horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            TextButton(R.string.next_sentence) {
                if (wrapper == null)
                    MainActivity.simpleToast(R.string.input_text_first)
                else if (sentence < wrapper!!.maxSentence) {
                    sentence++
                    outputText = wrapper!!.untilSentence(sentence)
                }
                else MainActivity.simpleToast(R.string.last_sentence)
            }
            Spacer(Modifier.padding(4.dp))
            TextButton(R.string.next_phase) {
                if (wrapper == null)
                    MainActivity.simpleToast(R.string.input_text_first)
                else {
                    val wrapper1 = wrapper!!
                    if (wrapper1.phase < wrapper1.maxPhase) {
                        sentence = 0
                        wrapper1.phase++
                        outputText = wrapper1.toString()
                    } else MainActivity.simpleToast(R.string.last_phase)
                }
            }
            Spacer(Modifier.padding(4.dp))
            TextButton(R.string.reset_phase) {
                if (wrapper == null)
                    MainActivity.simpleToast(R.string.input_text_first)
                else {
                    outputText = ""
                    sentence = 0
                    wrapper!!.phase = 0
                }
            }
        }
        Text(outputText, fontSize = 20.sp, modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ReciterAppPreview() {

}

@Composable
fun TextButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) { Text(text) }
}

@Composable
fun TextButton(textId: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier) { Text(stringResource(textId)) }
}
