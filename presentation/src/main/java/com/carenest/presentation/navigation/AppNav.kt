package com.carenest.presentation.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.carenest.designsystem.components.bottomnav.BottomNavItem
import com.carenest.designsystem.components.bottomnav.SPBottomNavigation
import com.carenest.designsystem.components.toast.SnackbarHost
import com.carenest.designsystem.components.topbar.BaseTopAppBar
import com.carenest.designsystem.components.topbar.TopBarAction
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.LocationDetails
import com.carenest.domain.model.settings.ThemeMode
import com.carenest.presentation.MainViewModel
import com.carenest.presentation.R
import com.carenest.presentation.navigation.NavigationConfig.savedStateConfiguration
import com.carenest.presentation.ui.aichat.chat.AIChatScreen
import com.carenest.presentation.ui.aichat.choosepatient.ChoosePatientScreen
import com.carenest.presentation.ui.aichat.emergency.EmergencyAssistanceScreen
import com.carenest.presentation.ui.auth.login.LoginScreen
import com.carenest.presentation.ui.auth.otp.OtpScreen
import com.carenest.presentation.ui.auth.register.RegisterScreen
import com.carenest.presentation.ui.auth.register.PersonalInformationMode
import com.carenest.presentation.ui.history.HistoryScreen
import com.carenest.presentation.ui.history_details.ServiceHistoryDetailsScreen
import com.carenest.presentation.ui.chat.ChatScreen
import com.carenest.presentation.ui.home.HomeScreen
import com.carenest.presentation.ui.map.MapScreen
import com.carenest.presentation.ui.onBoarding.OnBoardingScreen
import com.carenest.presentation.ui.profile_completion.ProfileCompletionScreen
import com.carenest.presentation.ui.profile.ProfileScreen
import com.carenest.presentation.ui.qrcode.QrCodeScreen
import com.carenest.presentation.ui.request_service.RequestServiceScreen
import com.carenest.presentation.ui.search_for_nurse.NurseSearchScreen
import com.carenest.presentation.ui.servicedetails.ServiceDetailsScreen
import com.carenest.presentation.ui.servicelist.ServicesScreen
import com.carenest.presentation.ui.splash.SplashScreen
import com.carenest.presentation.ui.tracking.NurseOnTheWayScreen
import com.carenest.presentation.ui.visit_summary.VisitCompletedScreen
import com.carenest.presentation.ui.wallet.AddFundsScreen
import com.carenest.presentation.ui.wallet.AddPaymentMethodScreen
import com.carenest.presentation.ui.wallet.WalletScreen
import com.carenest.presentation.ui.family_members.add.AddFamilyMemberScreenRoute
import com.carenest.presentation.ui.family_members.members.FamilyMembersScreen
import com.carenest.presentation.ui.profile_completion.ProfileCompletionSource
import com.carenest.presentation.ui.settings.SettingsScreen
import kotlinx.coroutines.launch
import com.carenest.designsystem.R as RD

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(
    deepLinkRequestId: String? = null,
    onDeepLinkHandled: () -> Unit = {},
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainState by mainViewModel.state.collectAsStateWithLifecycle()

    if (!mainState.isReady) return

    val isDarkTheme = when (mainState.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    SpTheme(
        isDarkTheme = isDarkTheme,
        languageCode = mainState.languageCode
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        
        var showNotificationRationale by remember { mutableStateOf(false) }
        val notificationsDisabledMessage = stringResource(R.string.notifications_disabled_message)

        if (mainState.isLoggedIn) {
            com.carenest.presentation.util.NotificationPermissionHandler(
                onPermissionGranted = {
                    showNotificationRationale = false
                },
                onPermissionDenied = {
                    showNotificationRationale = true
                },
                showRationale = showNotificationRationale,
                onRationaleDismissed = {
                    showNotificationRationale = false
                    coroutineScope.launch { snackbarHostState.showSnackbar(notificationsDisabledMessage) }
                }
            )
        }

        val onShowSnackbar: (String) -> Unit = { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }

        val initialRoute: NavKey = if (deepLinkRequestId != null) AppRoute.Home else AppRoute.Splash

        var mapResultLocation by remember { mutableStateOf<LocationDetails?>(null) }
        // Retained destinations need an explicit trigger to reload after successful add/edit flows.
        var familyMembersReloadTrigger by remember { mutableStateOf(0) }
        var profileReloadTrigger by remember { mutableStateOf(0) }
        var requestServiceReloadTrigger by remember { mutableStateOf(0) }

        val backStack = rememberNavBackStack(
            savedStateConfiguration, initialRoute
        )

        LaunchedEffect(deepLinkRequestId) {
            if (deepLinkRequestId != null) {
                Snapshot.withMutableSnapshot {
                    backStack.clear()
                    backStack.add(AppRoute.Home)
                    backStack.add(AppRoute.NurseOnTheWay(deepLinkRequestId))
                }
                onDeepLinkHandled()
            }
        }

        LaunchedEffect(mainState.isLoggedIn) {
            if (!mainState.isLoggedIn && backStack.lastOrNull() != AppRoute.Login && backStack.lastOrNull() != AppRoute.Splash && backStack.lastOrNull() != AppRoute.OnBoarding) {
                Snapshot.withMutableSnapshot {
                    backStack.clear()
                    backStack.add(AppRoute.Login)
                }
            }
        }

        val topBarState = remember { mutableStateOf(TopBarConfiguration()) }

        CompositionLocalProvider(LocalTopBarState provides topBarState) {
            val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
                fun replaceWith(route: NavKey) {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(route)
                    }
                }

                entry<AppRoute.Splash> {
                    SplashScreen(
                        onNavigateToOnBoarding = { replaceWith(AppRoute.OnBoarding) },
                        onNavigateToHome = { replaceWith(AppRoute.Home) },
                        onNavigateToLogin = { replaceWith(AppRoute.Login) },
                        onNavigateToRegister = { replaceWith(AppRoute.Register) },
                        onNavigateToCompleteProfile = { replaceWith(AppRoute.ProfileCompletion()) },
                        onNavigateToTracking = { requestId -> replaceWith(AppRoute.NurseOnTheWay(requestId)) },
                        onNavigateToSearch = { requestId -> 
                            replaceWith(AppRoute.SearchForNurse(reservationId = requestId, serviceRequestId = requestId)) 
                        }
                    )
                }

                entry<AppRoute.Login> {
                    LoginScreen(
                        onNavigateToOtp = { phone, otp, method, pendingToken ->
                            backStack.add(AppRoute.Otp(phone = phone, otp = otp, method = method, pendingToken = pendingToken))
                        },
                        onNavigateToHome = { replaceWith(AppRoute.Home) }
                    )
                }

                entry<AppRoute.Otp> { route ->
                    OtpScreen(
                        entry = route,
                        onNavigateToRegister = { replaceWith(AppRoute.Register) },
                        onNavigateToCompleteProfile = { replaceWith(AppRoute.ProfileCompletion()) },
                        onNavigateToHome = { replaceWith(AppRoute.Home) },
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.Register> {
                    RegisterScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToWelcome = { _ -> replaceWith(AppRoute.ProfileCompletion()) },
                        onNavigateHome = { replaceWith(AppRoute.Home) }
                    )
                }

                entry<AppRoute.ProfileCompletion> { route ->
                    ProfileCompletionScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToHome = { replaceWith(AppRoute.Home) },
                        onNavigateToFamilyMembers = {
                            familyMembersReloadTrigger += 1
                            replaceWith(AppRoute.FamilyMembers)
                        },
                        isEditMode = route.isEditMode,
                        onEditComplete = {
                            familyMembersReloadTrigger += 1
                            profileReloadTrigger += 1
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    )
                }

                entry<AppRoute.Home> {
                    HomeScreen(
                        onNavigateToServices = { replaceWith(AppRoute.Services) },
                        onNavigateToHistory = { replaceWith(AppRoute.History) },
                        onNavigateToAIChat = { backStack.add(AppRoute.ChoosePatient) },
                        onNavigateToServiceDetails = { serviceId -> backStack.add(AppRoute.ServiceDetails(serviceId)) },
                        onNavigateToServiceHistoryDetails = { requestId -> backStack.add(AppRoute.ServiceHistoryDetails(requestId)) },
                        onNavigateToActiveRequest = { requestId, status ->
                            if (status.equals("SEARCHING", ignoreCase = true) || status.equals("PENDING", ignoreCase = true)) {
                                backStack.add(AppRoute.SearchForNurse(requestId, requestId))
                            } else {
                                backStack.add(AppRoute.NurseOnTheWay(requestId))
                            }
                        },
                        onNavigateToProfile = { backStack.add(AppRoute.Profile) }
                    )
                }

                entry<AppRoute.Services> {
                    ServicesScreen(
                        onNavigateToDetails = { serviceId -> backStack.add(AppRoute.ServiceDetails(serviceId)) },
                        onNavigateToAIChat = { backStack.add(AppRoute.ChoosePatient) }
                    )
                }

                entry<AppRoute.ServiceDetails> { route ->
                    ServiceDetailsScreen(
                        serviceId = route.serviceId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onRequestService = { serviceId ->
                            backStack.add(AppRoute.RequestService(serviceId = serviceId, isFromAi = route.isFromAi))
                        }
                    )
                }

                entry<AppRoute.OnBoarding> {
                    OnBoardingScreen(onNavigateToHome = { replaceWith(AppRoute.Login) })
                }

                entry<AppRoute.History> {
                    HistoryScreen(
                        onNavigateBack = { 
                            if (backStack.size > 1) backStack.removeLastOrNull()
                            else replaceWith(AppRoute.Home)
                        },
                        onNavigateToDetails = { historyId: String -> backStack.add(AppRoute.ServiceHistoryDetails(historyId)) },
                        onNavigateToServices = { replaceWith(AppRoute.Services) }
                    )
                }

                entry<AppRoute.ServiceHistoryDetails> { route ->
                    ServiceHistoryDetailsScreen(
                        requestId = route.requestId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.Profile> {
                    ProfileScreen(
                        reloadTrigger = profileReloadTrigger,
                        onNavigateToPersonalInfo = { backStack.add(AppRoute.PersonalInfo) },
                        onNavigateToHealthProfile = { backStack.add(AppRoute.HealthProfile) },
                        onNavigateToFamilyMembers = { backStack.add(AppRoute.FamilyMembers) },
                        onNavigateToPayment = { backStack.add(AppRoute.Wallet) },
                        onNavigateToSettings = { backStack.add(AppRoute.Settings) },
                        onLogout = { replaceWith(AppRoute.Login) },
                        onShowMessage = onShowSnackbar
                    )
                }

                entry<AppRoute.PersonalInfo> {
                    RegisterScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToWelcome = {},
                        onNavigateHome = {},
                        mode = PersonalInformationMode.EditProfile,
                        onEditComplete = {
                            profileReloadTrigger += 1
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    )
                }

                entry<AppRoute.HealthProfile> {
                    ProfileCompletionScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToHome = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        isEditMode = true,
                        onEditComplete = {
                            profileReloadTrigger += 1
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    )
                }

                entry<AppRoute.Settings> {
                    SettingsScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onShowMessage = onShowSnackbar
                    )
                }

                entry<AppRoute.Wallet> {
                    WalletScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onAddFunds = { backStack.add(AppRoute.AddFunds) },
                        onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) }
                    )
                }

                entry<AppRoute.AddFunds> {
                    AddFundsScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) },
                        onTermsClick = {},
                        onAddFunds = {}
                    )
                }

                entry<AppRoute.AddPaymentMethod> {
                    AddPaymentMethodScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onCreditCardClick = {},
                        onPayPalClick = {},
                        onFawryCashClick = {},
                        onMeezaCardClick = {},
                        onMobileWalletClick = {}
                    )
                }

                entry<AppRoute.RequestService> { route ->
                    RequestServiceScreen(
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },

                        onNavigateToMap = {
                            backStack.add(AppRoute.Map)
                        },

                        onNavigateToEditProfile = {
                            backStack.add(
                                AppRoute.ProfileCompletion(
                                    isEditMode = true
                                )
                            )
                        },

                        // Add a family member from Request Service
                        onNavigateToAddPatient = {
                            backStack.add(
                                AppRoute.AddFamilyMember()
                            )
                        },

                        onNavigateToServiceSelection = {
                            backStack.add(AppRoute.Services)
                        },

                        onNavigateToAddressPicker = {
                            // TODO
                        },

                        onSubmitRequestClick = { serviceRequestId ->
                            backStack.add(
                                AppRoute.SearchForNurse(
                                    reservationId = serviceRequestId,
                                    serviceRequestId = serviceRequestId
                                )
                            )
                        },

                        selectServiceId = route.serviceId,

                        isFromAi = route.isFromAi,

                        mapResultLocation = mapResultLocation,

                        onMapResultConsumed = {
                            mapResultLocation = null
                        },

                        reloadTrigger = requestServiceReloadTrigger
                    )
                }

                entry<AppRoute.Map> {
                    MapScreen(
                        onLocationConfirmed = { locationDetails ->
                            mapResultLocation = locationDetails
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        },
                        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.SearchForNurse> { route ->
                    NurseSearchScreen(
                        reservationId = route.reservationId,
                        serviceRequestId = route.serviceRequestId,
                        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onMatched = { requestId -> replaceWith(AppRoute.NurseOnTheWay(requestId)) }
                    )
                }

                entry<AppRoute.NurseOnTheWay> { route ->
                    NurseOnTheWayScreen(
                        requestId = route.requestId,
                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            } else {
                                replaceWith(AppRoute.Home)
                            }
                        },
                        onNavigateToQrCode = { backStack.add(AppRoute.QrCode(route.requestId)) },
                        onOpenChat = { _ -> backStack.add(AppRoute.Chat(route.requestId)) },
                        showSnackbar = onShowSnackbar,
                        onVisitCompleted = { requestId -> replaceWith(AppRoute.VisitCompleted(requestId)) }
                    )
                }

                entry<AppRoute.QrCode> { route ->
                    QrCodeScreen(
                        requestId = route.requestId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onVisitCompleted = { requestId -> replaceWith(AppRoute.VisitCompleted(requestId)) }
                    )
                }

                entry<AppRoute.ChoosePatient> {
                    ChoosePatientScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToChat = { patientId -> backStack.add(AppRoute.AIChat(patientId)) },
                        onNavigateToAddFamilyMember = { backStack.add(AppRoute.AddFamilyMember()) }
                    )
                }

                entry<AppRoute.FamilyMembers> {
                    FamilyMembersScreen(
                        onNavigateBack = {
                            profileReloadTrigger += 1
                            if (backStack.size > 1) backStack.removeLastOrNull()
                        },
                        onNavigateToAddMember = { memberId -> backStack.add(AppRoute.AddFamilyMember(memberId)) },
                        onNavigateToEditHealthProfile = { memberId ->
                            backStack.add(
                                AppRoute.ProfileCompletion(
                                    profileId = memberId,
                                    isEditMode = true,
                                    source = com.carenest.presentation.ui.profile_completion.ProfileCompletionSource.FAMILY_MEMBER
                                )
                            )
                        },
                        reloadTrigger = familyMembersReloadTrigger,
                        onShowMessage = onShowSnackbar
                    )
                }

//                entry<AppRoute.AddFamilyMember> { route ->
//                    AddFamilyMemberScreenRoute(
//                        memberId = route.memberId,
//                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
//                        onMemberSaved = {
//                            familyMembersReloadTrigger += 1
//                            requestServiceReloadTrigger += 1
//                        },
//                        onNavigateToCompleteProfile = { newMemberId ->
//                            backStack.add(
//                                AppRoute.ProfileCompletion(
//                                    profileId = newMemberId,
//                                    isEditMode = false,
//                                    source = com.carenest.presentation.ui.profile_completion.ProfileCompletionSource.FAMILY_MEMBER
//                                )
//                            )
//                        },
//                        onShowMessage = onShowSnackbar
//                    )
//                }

                entry<AppRoute.AddFamilyMember> { route ->
                    AddFamilyMemberScreenRoute(
                        memberId = route.memberId,

                        onNavigateBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },

                        onMemberSaved = {
                            familyMembersReloadTrigger += 1
                            requestServiceReloadTrigger += 1

                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            }
                        },

                        onNavigateToCompleteProfile = { newMemberId ->
                            backStack.add(
                                AppRoute.ProfileCompletion(
                                    profileId = newMemberId,
                                    isEditMode = false,
                                    source = ProfileCompletionSource.FAMILY_MEMBER
                                )
                            )
                        },

                        onShowMessage = onShowSnackbar
                    )
                }

                entry<AppRoute.AIChat> { route ->
                    AIChatScreen(
                        patientId = route.patientId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToBookings = { replaceWith(AppRoute.History) },
                        onNavigateToServiceDetails = { categoryStr ->
                            backStack.add(AppRoute.ServiceDetails(serviceId = categoryStr, isFromAi = true))
                        },
                        onNavigateToRequestService = { serviceId ->
                            backStack.add(AppRoute.RequestService(serviceId = serviceId, isFromAi = true))
                        },
                        onShowMessage = onShowSnackbar
                    )
                }

                entry<AppRoute.EmergencyAssistance> {
                    EmergencyAssistanceScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onCallFamilyMember = { backStack.add(AppRoute.AddFamilyMember()) },
                        onDismiss = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.VisitCompleted> { route ->
                    VisitCompletedScreen(
                        requestId = route.requestId,
                        onNavigateHome = { replaceWith(AppRoute.Home) },
                        onShowSnackbar = onShowSnackbar
                    )
                }

                entry<AppRoute.Chat> { route ->
                    ChatScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        requestId = route.requestId,
                        showSnackbar = onShowSnackbar
                    )
                }
            }

            val bottomNavRoutes = remember {
                listOf<AppRoute>(
                    AppRoute.Home,
                    AppRoute.Services,
                    AppRoute.History,
                    AppRoute.Profile,
                )
            }

            val currentRoute = backStack.lastOrNull()
            val selectedIndex = if (currentRoute != null) {
                bottomNavRoutes.indexOfFirst { it::class == currentRoute::class }
            } else -1

            val shouldShowBottomBar = selectedIndex != -1
            val shouldHandleBackToHome = currentRoute != null && currentRoute != AppRoute.Home && currentRoute !in listOf(
                AppRoute.Splash,
                AppRoute.OnBoarding,
                AppRoute.Login,
            ) && currentRoute !is AppRoute.SearchForNurse

            BackHandler(enabled = shouldHandleBackToHome) {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                } else {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(AppRoute.Home)
                    }
                }
            }

            fun onBottomNavItemSelected(index: Int) {
                val targetRoute = bottomNavRoutes.getOrNull(index) ?: return
                val current = backStack.lastOrNull()
                // Check if targetRoute is already the current route by comparing classes
                if (current == null || current::class != targetRoute::class) {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(targetRoute)
                    }
                }
            }

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding()
                    .imePadding(),
                containerColor = Theme.colors.backGround,
                contentWindowInsets = WindowInsets(0),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    val config = topBarState.value
                    if (config.title != null) {
                        BaseTopAppBar(
                            title = config.title,
                            leadingIcon = if (config.showLeadingIcon) {
                                config.leadingIcon ?: painterResource(id = RD.drawable.ic_arrow_back)
                            } else null,
                            onLeadingClick = config.onLeadingClick,
                            autoMirrorLeadingIcon = true,
                            actions = buildList {
                                if (config.profileImage != null) {
                                    add(TopBarAction(config.profileImage, "Profile", onClick = config.onProfileClick ?: {}))
                                }
                                config.trailingAction?.let(::add)
                            },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    NavDisplay<NavKey>(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                            .then(
                                if (shouldShowBottomBar) Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                                else Modifier
                            ),
                        entries = rememberDecoratedNavEntries(
                            backStack = backStack,
                            entryProvider = entryProvider,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            )
                        ),
                        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    )

                    if (shouldShowBottomBar) {
                        SPBottomNavigation(
                            items = listOf(
                                BottomNavItem(
                                    stringResource(R.string.nav_home),
                                    RD.drawable.ic_bottom_nav_home,
                                    RD.drawable.ic_bottom_nav_home_selected,
                                ),
                                BottomNavItem(
                                    stringResource(R.string.nav_services),
                                    RD.drawable.ic_bottom_nav_services,
                                    RD.drawable.ic_bottom_nav_services_selected,
                                ),
                                BottomNavItem(
                                    stringResource(R.string.nav_booking),
                                    RD.drawable.ic_bottom_nav_bookings,
                                    RD.drawable.ic_bottom_nav_bookings_selected,
                                ),
                                BottomNavItem(
                                    stringResource(R.string.nav_profile),
                                    RD.drawable.ic_bottom_nav_profile,
                                    RD.drawable.ic_bottom_nav_profile_selected,
                                )
                            ),
                            selectedIndex = selectedIndex,
                            onItemSelected = ::onBottomNavItemSelected,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = paddingValues.calculateBottomPadding()),
                        )
                    }
                }
            }
        }
    }
}
