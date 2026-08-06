package com.carenest.presentation.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.carenest.presentation.MainViewModel
import com.carenest.presentation.R
import com.carenest.presentation.navigation.NavigationConfig.savedStateConfiguration
import com.carenest.presentation.ui.aichat.chat.AIChatScreen
import com.carenest.presentation.ui.aichat.choosepatient.ChoosePatientScreen
import com.carenest.presentation.ui.aichat.emergency.EmergencyAssistanceScreen
import com.carenest.presentation.ui.auth.login.LoginScreen
import com.carenest.presentation.ui.auth.otp.OtpScreen
import com.carenest.presentation.ui.auth.register.RegisterScreen
import com.carenest.presentation.ui.history.HistoryScreen
import com.carenest.presentation.ui.history_details.ServiceHistoryDetailsScreen
import com.carenest.presentation.ui.chat.ChatScreen
import com.carenest.presentation.ui.home.HomeScreen
import com.carenest.presentation.ui.map.MapScreen
import com.carenest.presentation.ui.onBoarding.OnBoardingScreen
import com.carenest.presentation.ui.profile_completion.ProfileCompletionScreen
import com.carenest.presentation.ui.profile.ProfileScreen
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
import kotlinx.coroutines.launch
import com.carenest.designsystem.R as RD

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainState by mainViewModel.state.collectAsStateWithLifecycle()

    if (!mainState.isReady) return

    SpTheme(
        isDarkTheme = mainState.isDarkTheme,
        languageCode = mainState.languageCode
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()

        val onShowSnackbar: (String) -> Unit = { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }

        val initialRoute: NavKey = AppRoute.Splash

        var mapResultLocation by remember { mutableStateOf<LocationDetails?>(null) }

        val backStack = rememberNavBackStack(
            savedStateConfiguration, initialRoute
        )

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
                        onNavigateToCompleteProfile = { replaceWith(AppRoute.ProfileCompletion) }
                    )
                }

                entry<AppRoute.Login> {
                    LoginScreen(
                        onNavigateToOtp = { phone, otp, method ->
                            backStack.add(AppRoute.Otp(phone = phone, otp = otp, method = method))
                        }
                    )
                }

                entry<AppRoute.Otp> { route ->
                    OtpScreen(
                        entry = route,
                        onNavigateToRegister = { replaceWith(AppRoute.Register) },
                        onNavigateToCompleteProfile = { replaceWith(AppRoute.ProfileCompletion) },
                        onNavigateToHome = { replaceWith(AppRoute.Home) },
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.Register> {
                    RegisterScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToWelcome = { replaceWith(AppRoute.ProfileCompletion) },
                        onNavigateHome = { replaceWith(AppRoute.Home) }
                    )
                }

                entry<AppRoute.ProfileCompletion> {
                    ProfileCompletionScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToHome = { replaceWith(AppRoute.Home) }
                    )
                }

                entry<AppRoute.Home> {
                    HomeScreen(
                        onNavigateToServices = { replaceWith(AppRoute.Services) },
                        onNavigateToHistory = { replaceWith(AppRoute.History) },
                        onNavigateToAIChat = { backStack.add(AppRoute.ChoosePatient) },
                        onNavigateToServiceDetails = { serviceId -> backStack.add(AppRoute.ServiceDetails(serviceId)) },
                        onNavigateToServiceHistoryDetails = { requestId -> backStack.add(AppRoute.ServiceHistoryDetails(requestId)) }
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
                        onRequestService = { serviceId -> backStack.add(AppRoute.RequestService(serviceId)) }
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
                        onNavigateToFamilyMembers = { backStack.add(AppRoute.FamilyMembers) }, onLogout = { replaceWith(AppRoute.Login) }
                    )
                }

                entry<AppRoute.Wallet> {
                    WalletScreen(
                        onAddFunds = { backStack.add(AppRoute.AddFunds) },
                        onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) }
                    )
                }

                entry<AppRoute.AddFunds> {
                    AddFundsScreen(
                        onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) },
                        onTermsClick = {},
                        onAddFunds = {}
                    )
                }

                entry<AppRoute.AddPaymentMethod> {
                    AddPaymentMethodScreen({}, {}, {}, {}, {})
                }

                entry<AppRoute.RequestService> { route ->
                    RequestServiceScreen(
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToMap = { backStack.add(AppRoute.Map) },
                        onNavigateToEditProfile = { /* TODO */ },
                        onNavigateToAddPatient = { /* TODO */ },
                        onNavigateToServiceSelection = { backStack.add(AppRoute.Services) },
                        onNavigateToAddressPicker = { /* TODO */ },
                        onSubmitRequestClick = { backStack.add(AppRoute.SearchForNurse) },
                        selectServiceId = route.serviceId,
                        mapResultLocation = mapResultLocation,
                        onMapResultConsumed = { mapResultLocation = null }
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

                entry<AppRoute.SearchForNurse> {
                    NurseSearchScreen(
                        onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onMatched = { replaceWith(AppRoute.AcceptOffer) }
                    )
                }

                entry<AppRoute.NurseOnTheWay> { route ->
                    NurseOnTheWayScreen(
                        requestId = route.requestId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToQrCode = {},
                        onOpenChat = { nurseId -> /* TODO */ },
                        showSnackbar = onShowSnackbar
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
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToAddMember = { memberId -> backStack.add(AppRoute.AddFamilyMember(memberId)) }
                    )
                }

                entry<AppRoute.AddFamilyMember> { route ->
                    AddFamilyMemberScreenRoute(
                        memberId = route.memberId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() }
                    )
                }

                entry<AppRoute.AIChat> { route ->
                    AIChatScreen(
                        patientId = route.patientId,
                        onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                        onNavigateToBookings = { replaceWith(AppRoute.History) },
                        onNavigateToServiceDetails = { categoryStr ->
                            backStack.add(AppRoute.ServiceDetails(categoryStr))
                        }
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
                    AppRoute.Wallet,
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
                AppRoute.Login
            )

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
                                BottomNavItem(stringResource(R.string.nav_home), RD.drawable.ic_bottom_nav_home),
                                BottomNavItem(stringResource(R.string.nav_services), RD.drawable.ic_bottom_nav_services),
                                BottomNavItem(stringResource(R.string.nav_booking), RD.drawable.ic_bottom_nav_bookings),
                                BottomNavItem(stringResource(R.string.nav_profile), RD.drawable.ic_bottom_nav_profile),
                                BottomNavItem(stringResource(R.string.nav_wallet), RD.drawable.ic_bottom_nav_wallet)
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
