package com.block.employeedirectory.ui.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.block.employeedirectory.ui.adapter.ImageCache
import com.block.employeedirectory.ui.adapter.ImageDownloader
import com.block.employeedirectory.ui.model.EmployeeItemUiState
import com.block.employeedirectory.ui.viewmodel.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class EmployeeActivity: AppCompatActivity() {
    private val employeeViewModel: EmployeeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenState()
        }
        //get employee data
        employeeViewModel.getEmployees()
    }

    //loading states
    @Composable
    fun ScreenState(viewModel: EmployeeViewModel = viewModel()) = when(viewModel.employeeUiScreenState) {
        com.block.employeedirectory.ui.model.ScreenState.LOADING -> LoadingState()
        com.block.employeedirectory.ui.model.ScreenState.ERROR -> ErrorState()
        com.block.employeedirectory.ui.model.ScreenState.EMPTY -> EmptyState()
        com.block.employeedirectory.ui.model.ScreenState.DATA -> EmployeeList()
    }

    @Composable
    fun EmptyState(message: String = "Wow, this is embarrassing..there's no data") {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                style = MaterialTheme.typography.body2,
                lineHeight = 20.sp,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun ErrorState(message: String = "Oops! Something went wrong, Please refresh after some time") {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(imageVector = Icons.Outlined.Error,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.error),
                modifier = Modifier.size(108.dp),
                contentDescription = null)
            Spacer(Modifier.size(12.dp))
            Text(text = message, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center, color = MaterialTheme.colors.error.copy(alpha = 0.9F))
        }
    }

    @Composable
    fun LoadingState() {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Loading")
                CircularProgressIndicator()
            }
        }
    }

    @Composable
    fun EmployeeList(viewModel: EmployeeViewModel = viewModel()) {

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(viewModel.employeeUiState.employeeItems,
                itemContent = {
                    EmployeeListItemCard(employee = it)
                })
        }
    }

    @Composable
    fun EmployeeListItemCard(employee: EmployeeItemUiState) {
        Card(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(corner = CornerSize(24.dp))

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val composableScope = rememberCoroutineScope()
                val imageCache = remember{ImageCache()}
                val bitmap: MutableState<ImageBitmap?> = mutableStateOf(null)

                employee.photoSmall?.let { photoUrl ->
                    loadImage(photoUrl, composableScope, imageCache) {
                        bitmap.value = it
                    }
                }

                EmployeeListItemImage(bitmap)
                EmployeeListItemSummary(employee)
            }
        }
    }

    @Composable
    fun EmployeeListItemImage(bitmapState: MutableState<ImageBitmap?>) {
        if(bitmapState.value != null) {
            Image(
                bitmap = bitmapState.value!!,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(84.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(24.dp)))
            )
        } else {
            Image(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(8.dp)
                    .size(84.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(24.dp)))
            )
        }
    }

    @Composable
    fun EmployeeListItemSummary(employee: EmployeeItemUiState) {
        Column() {
            Text(text = employee.name ?: "", style = MaterialTheme.typography.h6)
            Text(text = employee.team ?: "", style = MaterialTheme.typography.caption)
        }
    }

    //view logic for downloading an image and saving/retrieving from cache
    private fun loadImage(url: String,
                          scope: CoroutineScope,
                          imgCache: ImageCache, callback: (ImageBitmap?) -> Unit) {

        val imageCallback = object : ImageDownloader.ImageResultListener {
            override fun onError(err: Throwable) {
                callback(null)
            }

            override fun onSuccess(success: ImageBitmap) {
                callback(success)
            }
        }

        var imageLoader : ImageDownloader?

        scope.launch {
            imgCache.initializeCache(this@EmployeeActivity.applicationContext)
            imageLoader = ImageDownloader(imgCache, 100, 100)
            imageLoader?.setListener(imageCallback)
            imageLoader?.getImageResult(url)
        }
    }
}