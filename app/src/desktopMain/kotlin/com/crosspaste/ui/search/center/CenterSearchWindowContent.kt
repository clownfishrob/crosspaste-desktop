package com.crosspaste.ui.search.center

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.materialsymbols.MaterialSymbols
import com.composables.icons.materialsymbols.rounded.Search
import com.composables.icons.materialsymbols.rounded.Settings
import com.composables.icons.materialsymbols.roundedfilled.Push_pin
import com.crosspaste.app.DesktopAppWindowManager
import com.crosspaste.app.WindowTrigger
import com.crosspaste.db.paste.PasteTagDao
import com.crosspaste.i18n.GlobalCopywriter
import com.crosspaste.paste.DesktopPasteMenuService
import com.crosspaste.paste.PasteType
import com.crosspaste.paste.item.PasteItem
import com.crosspaste.ui.LocalDesktopAppSizeValueState
import com.crosspaste.ui.LocalSearchWindowInfoState
import com.crosspaste.ui.LocalThemeExtState
import com.crosspaste.ui.NavigationManager
import com.crosspaste.ui.Settings
import com.crosspaste.ui.base.CustomTextField
import com.crosspaste.ui.base.GeneralIconButton
import com.crosspaste.ui.base.KeyboardView
import com.crosspaste.ui.base.PasteContextMenuView
import com.crosspaste.ui.base.enter
import com.crosspaste.ui.model.FocusedElement
import com.crosspaste.ui.model.PasteSearchViewModel
import com.crosspaste.ui.model.PasteSelectionViewModel
import com.crosspaste.ui.model.RequestPasteListFocus
import com.crosspaste.ui.model.RequestSearchInputFocus
import com.crosspaste.ui.paste.PasteDataScope
import com.crosspaste.ui.paste.PasteEmptyScreenView
import com.crosspaste.ui.paste.createPasteDataScope
import com.crosspaste.ui.paste.side.preview.SidePreviewView
import com.crosspaste.ui.paste.side.quickSlotIndex
import com.crosspaste.ui.search.side.SearchTagsView
import com.crosspaste.ui.theme.AppUIColors
import com.crosspaste.ui.theme.AppUISize.large2X
import com.crosspaste.ui.theme.AppUISize.medium
import com.crosspaste.ui.theme.AppUISize.mediumRoundedCornerShape
import com.crosspaste.ui.theme.AppUISize.small
import com.crosspaste.ui.theme.AppUISize.small2X
import com.crosspaste.ui.theme.AppUISize.small3X
import com.crosspaste.ui.theme.AppUISize.tiny
import com.crosspaste.ui.theme.AppUISize.tiny2X
import com.crosspaste.ui.theme.AppUISize.tiny2XRoundedCornerShape
import com.crosspaste.ui.theme.AppUISize.tiny5X
import com.crosspaste.ui.theme.AppUISize.tinyRoundedCornerShape
import com.crosspaste.ui.theme.AppUISize.xLarge
import com.crosspaste.ui.theme.AppUISize.xxLarge
import com.crosspaste.ui.theme.ThemeExt
import com.crosspaste.utils.GlobalCoroutineScope.mainCoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Compact two-pane search overlay: category icon strip on top, item list with
 * quick-slot numbers on the left, one full preview on the right, search bar at
 * the bottom. Keyboard model: type to filter, Up/Down to move, Enter to paste,
 * Ctrl+1..9 (and Ctrl+0 for slot 10) for direct slot paste.
 */
@Composable
fun CenterSearchWindowContent() {
    val appWindowManager = koinInject<DesktopAppWindowManager>()
    val pasteSearchViewModel = koinInject<PasteSearchViewModel>()
    val pasteSelectionViewModel = koinInject<PasteSelectionViewModel>()

    val searchResult by pasteSearchViewModel.searchResults.collectAsState()

    var isCtrlPressed by remember { mutableStateOf(false) }
    var isShiftPressed by remember { mutableStateOf(false) }

    val latestSearchResult = rememberUpdatedState(searchResult)

    val scope = rememberCoroutineScope()

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(mediumRoundedCornerShape)
                .background(AppUIColors.generalBackground)
                .border(tiny5X, AppUIColors.lightBorderColor, mediumRoundedCornerShape)
                .onPreviewKeyEvent { event ->
                    isCtrlPressed = event.isCtrlPressed
                    isShiftPressed = event.isShiftPressed
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    when (event.key) {
                        Key.Enter -> {
                            scope.launch { pasteSelectionViewModel.toPaste() }
                            true
                        }
                        Key.DirectionUp -> {
                            pasteSelectionViewModel.selectPrev()
                            true
                        }
                        Key.DirectionDown -> {
                            pasteSelectionViewModel.selectNext()
                            true
                        }
                        Key.Escape -> {
                            scope.launch { appWindowManager.hideSearchWindow() }
                            true
                        }
                        else -> {
                            val slot = quickSlotIndex(event.key.nativeKeyCode)
                            if (event.isCtrlPressed && slot != null) {
                                mainCoroutineDispatcher.launch {
                                    latestSearchResult.value.getOrNull(slot)?.let {
                                        pasteSelectionViewModel.toPaste(it)
                                    }
                                }
                                true
                            } else {
                                false
                            }
                        }
                    }
                },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CenterSearchTopBar()
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                CenterItemList(
                    isCtrlPressed = isCtrlPressed,
                    isShiftPressed = isShiftPressed,
                )
                VerticalDivider(color = AppUIColors.lightBorderColor)
                CenterPreviewPane()
            }
            CenterSearchBottomBar()
        }
    }
}

// Filter target with a quiet hover highlight and a stronger selected state,
// shared by the "All" chip and the per-type icons in the top bar.
@Composable
private fun HoverableFilterBox(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier =
            Modifier
                .clip(tiny2XRoundedCornerShape)
                .hoverable(interactionSource)
                .background(
                    when {
                        selected -> MaterialTheme.colorScheme.surfaceContainerHighest
                        hovered -> MaterialTheme.colorScheme.surfaceContainerHigh
                        else -> Color.Transparent
                    },
                ).pointerInput(Unit) {
                    detectTapGestures { currentOnClick() }
                },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

private fun typeIconData(
    themeExt: ThemeExt,
    pasteType: PasteType,
) = when (pasteType) {
    PasteType.TEXT_TYPE -> themeExt.textTypeIconData
    PasteType.URL_TYPE -> themeExt.urlTypeIconData
    PasteType.HTML_TYPE -> themeExt.htmlTypeIconData
    PasteType.FILE_TYPE -> themeExt.fileTypeIconData
    PasteType.IMAGE_TYPE -> themeExt.imageTypeIconData
    PasteType.RTF_TYPE -> themeExt.rtfTypeIconData
    else -> themeExt.colorTypeIconData
}

@Composable
private fun CenterSearchTopBar() {
    val appWindowManager = koinInject<DesktopAppWindowManager>()
    val copywriter = koinInject<GlobalCopywriter>()
    val navigationManager = koinInject<NavigationManager>()
    val pasteSearchViewModel = koinInject<PasteSearchViewModel>()

    val appSizeValue = LocalDesktopAppSizeValueState.current
    val themeExt = LocalThemeExtState.current

    val searchBaseParams by pasteSearchViewModel.searchBaseParams.collectAsState()
    val selectedTypes = searchBaseParams.pasteTypeList

    val scope = rememberCoroutineScope()

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(appSizeValue.centerSearchTopBarHeight)
                .padding(horizontal = small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "All types" chip
        HoverableFilterBox(
            selected = selectedTypes.isEmpty(),
            onClick = { pasteSearchViewModel.updatePasteType(listOf()) },
        ) {
            Text(
                modifier = Modifier.padding(horizontal = small3X, vertical = tiny2X),
                text = copywriter.getText(PasteType.ALL_TYPES),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.width(tiny))

        // One icon per paste type; click toggles the single-type filter.
        PasteType.TYPES.forEach { pasteType ->
            val iconData = typeIconData(themeExt, pasteType)
            val selected = selectedTypes.singleOrNull() == pasteType.type
            HoverableFilterBox(
                selected = selected,
                onClick = {
                    pasteSearchViewModel.updatePasteType(
                        if (selected) listOf() else listOf(pasteType.type),
                    )
                },
            ) {
                Icon(
                    imageVector = iconData.imageVector,
                    contentDescription = copywriter.getText(pasteType.name),
                    tint = iconData.color,
                    modifier = Modifier.padding(tiny2X).size(large2X),
                )
            }
            Spacer(modifier = Modifier.width(tiny2X))
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.weight(2f).fillMaxHeight(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            SearchTagsView()
        }

        Spacer(modifier = Modifier.width(tiny))

        GeneralIconButton(
            imageVector = MaterialSymbols.Rounded.Settings,
            desc = "settings",
            colors =
                iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            buttonSize = xLarge,
            iconSize = large2X,
            shape = tiny2XRoundedCornerShape,
        ) {
            scope.launch {
                navigationManager.navigateAndClearStack(Settings)
                appWindowManager.showMainWindow(WindowTrigger.MENU)
                appWindowManager.hideSearchWindow()
            }
        }
    }
}

@Composable
private fun CenterItemList(
    isCtrlPressed: Boolean,
    isShiftPressed: Boolean,
) {
    val pasteMenuService = koinInject<DesktopPasteMenuService>()
    val pasteSearchViewModel = koinInject<PasteSearchViewModel>()
    val pasteSelectionViewModel = koinInject<PasteSelectionViewModel>()
    val pasteTagDao = koinInject<PasteTagDao>()

    val appSizeValue = LocalDesktopAppSizeValueState.current
    val searchWindowInfo = LocalSearchWindowInfoState.current

    val inputSearch by pasteSearchViewModel.inputSearch.collectAsState()
    val searchBaseParams by pasteSearchViewModel.searchBaseParams.collectAsState()
    val searchResult by pasteSearchViewModel.searchResults.collectAsState()
    val selectedIndexes by pasteSelectionViewModel.selectedIndexes.collectAsState()
    val loadAll by pasteSearchViewModel.loadAll.collectAsState()

    val searchListState = remember { LazyListState() }
    pasteSelectionViewModel.searchListState = searchListState
    val adapter = rememberScrollbarAdapter(scrollState = searchListState)
    val latestSearchResult = rememberUpdatedState(searchResult)

    // Primary collection colour per visible pinned item; re-emits when tag
    // membership changes (e.g. pinning via the row context menu).
    val tagColors by remember(searchResult) {
        pasteTagDao.getPasteTagColorsFlow(searchResult.map { it.id })
    }.collectAsState(initial = emptyMap())

    // Reset selection to the first match whenever the query or a filter changes.
    LaunchedEffect(
        inputSearch,
        searchBaseParams.sort,
        searchBaseParams.pasteTypeList,
        searchBaseParams.tag,
    ) {
        if (searchWindowInfo.show) {
            pasteSelectionViewModel.initSelectIndex()
            searchListState.scrollToItem(0)
        }
    }

    // Keep the selected row visible while navigating with the keyboard.
    LaunchedEffect(selectedIndexes) {
        val selectedIndex = selectedIndexes.firstOrNull() ?: return@LaunchedEffect
        val visibleItems = searchListState.layoutInfo.visibleItemsInfo
        if (visibleItems.none { it.index == selectedIndex }) {
            searchListState.animateScrollToItem(selectedIndex)
        }
    }

    // Load more when the viewport approaches the end of the loaded batch.
    LaunchedEffect(searchListState) {
        snapshotFlow {
            searchListState.layoutInfo.visibleItemsInfo.map { it.index }
        }.distinctUntilChanged().collect { visibleIndexes ->
            val lastDataIndex = visibleIndexes.lastOrNull { it < latestSearchResult.value.size }
            if (lastDataIndex != null &&
                latestSearchResult.value.size - lastDataIndex < visibleIndexes.size * 2
            ) {
                pasteSearchViewModel.tryAddLimit()
            }
        }
    }

    Box(
        modifier =
            Modifier
                .width(appSizeValue.centerSearchListWidth)
                .fillMaxHeight(),
    ) {
        LazyColumn(
            state = searchListState,
            modifier = Modifier.fillMaxSize().padding(horizontal = tiny),
        ) {
            itemsIndexed(
                searchResult,
                key = { _, item -> item.id },
                contentType = { _, item -> item.pasteType },
            ) { index, pasteData ->
                val currentIndex by rememberUpdatedState(index)
                val currentPasteData by rememberUpdatedState(pasteData)

                val rowScope =
                    remember(
                        currentPasteData.id,
                        currentPasteData.pasteState,
                        currentPasteData.pasteSearchContent,
                    ) {
                        createPasteDataScope(currentPasteData)
                    }

                rowScope?.CenterItemRow(
                    index = currentIndex,
                    selected = currentIndex in selectedIndexes,
                    pinColor = tagColors[currentPasteData.id]?.let { Color(it.toInt()) },
                    showSlotHighlight = isCtrlPressed,
                    onPress = {
                        pasteSelectionViewModel.clickSelectedIndex(currentIndex, isShiftPressed)
                    },
                    onDoubleTap = {
                        if (!isShiftPressed) {
                            pasteMenuService.quickPasteFromSearchWindow(currentPasteData)
                        }
                    },
                )
            }

            if (searchResult.isNotEmpty() && !loadAll) {
                item(key = "loading_indicator") {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(xxLarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(medium),
                            strokeWidth = tiny5X,
                            color =
                                MaterialTheme.colorScheme
                                    .contentColorFor(AppUIColors.generalBackground)
                                    .copy(alpha = 0.5f),
                        )
                    }
                }
            }
        }

        if (searchResult.isEmpty()) {
            val filterActive =
                inputSearch.isNotBlank() ||
                    searchBaseParams.pasteTypeList.isNotEmpty() ||
                    searchBaseParams.tag != null
            PasteEmptyScreenView(
                messageKey =
                    if (filterActive) {
                        "no_search_results"
                    } else {
                        "no_pasteboard_activity_detected_yet"
                    },
            )
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = adapter,
        )
    }
}

@Composable
private fun PasteDataScope.CenterItemRow(
    index: Int,
    selected: Boolean,
    pinColor: Color?,
    showSlotHighlight: Boolean,
    onPress: () -> Unit,
    onDoubleTap: () -> Unit,
) {
    val copywriter = koinInject<GlobalCopywriter>()
    val pasteMenuService = koinInject<DesktopPasteMenuService>()
    val themeExt = LocalThemeExtState.current

    val pasteType = remember(pasteData.id) { pasteData.getType() }
    val iconData = typeIconData(themeExt, pasteType)

    val title =
        remember(pasteData.id, pasteData.pasteSearchContent) {
            pasteData
                .getPasteItem(PasteItem::class)
                ?.getUserEditName()
                ?.takeIf { it.isNotBlank() }
                ?: pasteData.pasteSearchContent
                    ?.replace('\n', ' ')
                    ?.trim()
                    ?.take(80)
                    ?.takeIf { it.isNotBlank() }
        } ?: copywriter.getText(pasteData.getTypeName())

    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    PasteContextMenuView(
        items = pasteMenuService.sidePasteMenuItemsProvider(this),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(xxLarge)
                    .clip(tinyRoundedCornerShape)
                    .hoverable(interactionSource)
                    .background(
                        when {
                            selected -> MaterialTheme.colorScheme.secondaryContainer
                            hovered -> MaterialTheme.colorScheme.surfaceContainerHighest
                            else -> Color.Transparent
                        },
                    ).pointerInput(index) {
                        detectTapGestures(
                            onPress = {
                                onPress()
                                tryAwaitRelease()
                            },
                            onDoubleTap = { onDoubleTap() },
                        )
                    }.padding(horizontal = small3X),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Quick-slot number for the first 10 rows.
            Box(
                modifier = Modifier.width(large2X),
                contentAlignment = Alignment.Center,
            ) {
                if (index < 10) {
                    Text(
                        text = "${(index + 1) % 10}",
                        style = MaterialTheme.typography.labelMedium,
                        color =
                            if (showSlotHighlight) {
                                AppUIColors.importantColor
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                            },
                    )
                }
            }

            Spacer(modifier = Modifier.width(tiny2X))

            Icon(
                imageVector = iconData.imageVector,
                contentDescription = copywriter.getText(pasteType.name),
                tint = iconData.color,
                modifier = Modifier.size(medium),
            )

            Spacer(modifier = Modifier.width(small3X))

            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            pinColor?.let { color ->
                Spacer(modifier = Modifier.width(tiny2X))
                Icon(
                    imageVector = MaterialSymbols.RoundedFilled.Push_pin,
                    contentDescription = "pinned",
                    tint = color,
                    modifier = Modifier.size(small),
                )
            }
        }
    }
}

@Composable
private fun CenterPreviewPane() {
    val pasteSelectionViewModel = koinInject<PasteSelectionViewModel>()

    val currentPasteDataList by pasteSelectionViewModel.currentPasteDataList.collectAsState()
    val selectedIndexes by pasteSelectionViewModel.selectedIndexes.collectAsState()

    val selectedPasteData = currentPasteDataList.firstOrNull()
    val selectedIndex = selectedIndexes.firstOrNull() ?: 0

    Box(
        modifier = Modifier.fillMaxSize().padding(small),
        contentAlignment = Alignment.Center,
    ) {
        selectedPasteData?.let { pasteData ->
            val scope =
                remember(pasteData.id, pasteData.pasteState, pasteData.pasteSearchContent) {
                    createPasteDataScope(pasteData)
                }
            scope?.let {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = tinyRoundedCornerShape,
                    colors = CardDefaults.cardColors(containerColor = AppUIColors.pasteBackground),
                ) {
                    it.SidePreviewView(
                        showQuickSlot = false,
                        index = selectedIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterSearchBottomBar() {
    val appWindowManager = koinInject<DesktopAppWindowManager>()
    val copywriter = koinInject<GlobalCopywriter>()
    val pasteSearchViewModel = koinInject<PasteSearchViewModel>()
    val pasteSelectionViewModel = koinInject<PasteSelectionViewModel>()

    val appSizeValue = LocalDesktopAppSizeValueState.current
    val searchWindowInfo = LocalSearchWindowInfoState.current

    val inputSearch by pasteSearchViewModel.inputSearch.collectAsState()
    val prevAppName by appWindowManager.getPrevAppName().collectAsState(null)

    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(searchWindowInfo.show) {
        if (searchWindowInfo.show) {
            pasteSearchViewModel.resetSearch()
            searchFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(Unit) {
        pasteSelectionViewModel.uiEvent.collect { event ->
            when (event) {
                // In the center layout the search input is the primary focus
                // target: route both focus events to it and let the window's
                // preview key handler drive list navigation.
                RequestSearchInputFocus, RequestPasteListFocus -> {
                    if (searchFocusRequester.requestFocus()) {
                        pasteSelectionViewModel.setFocusedElement(FocusedElement.SEARCH_INPUT)
                    }
                }
            }
        }
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(appSizeValue.centerSearchBottomBarHeight)
                .padding(horizontal = small, vertical = tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomTextField(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(tinyRoundedCornerShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .focusRequester(searchFocusRequester)
                    .onFocusEvent {
                        if (it.isFocused) {
                            pasteSelectionViewModel.setFocusedElement(FocusedElement.SEARCH_INPUT)
                        }
                    },
            value = inputSearch,
            leadingIcon = {
                Icon(
                    imageVector = MaterialSymbols.Rounded.Search,
                    contentDescription = "search",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            onValueChange = { pasteSearchViewModel.updateInputSearch(it) },
            keyboardOptions = KeyboardOptions.Default.copy(autoCorrectEnabled = true),
            visualTransformation = VisualTransformation.None,
            placeholder = {
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = copywriter.getText("search_pasteboard"),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            isError = false,
            singleLine = true,
            contentPadding = PaddingValues(0.dp),
            colors =
                TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.width(small2X))

        prevAppName?.let {
            Text(
                text = "${copywriter.getText("paste_to")} $it",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(tiny))
        }

        KeyboardView(key = enter)
        Spacer(modifier = Modifier.width(tiny))
        KeyboardView(key = "Ctrl+1…9")
    }
}
