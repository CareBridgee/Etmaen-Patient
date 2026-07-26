package com.carenest.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
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
import com.carenest.presentation.R
import com.carenest.presentation.navigation.NavigationConfig.savedStateConfiguration
import com.carenest.presentation.ui.auth.login.LoginScreen
import com.carenest.presentation.ui.auth.otp.OtpScreen
import com.carenest.presentation.ui.auth.register.RegisterScreen
import com.carenest.presentation.ui.bookings.BookingsScreen
import com.carenest.presentation.ui.chat.ChatScreen
import com.carenest.presentation.ui.home.HomeScreen
import com.carenest.presentation.ui.map.MapScreen
import com.carenest.presentation.ui.onBoarding.OnBoardingScreen
import com.carenest.presentation.ui.splash.SplashScreen
import com.carenest.presentation.ui.profile.ProfileCompletionScreen
import com.carenest.presentation.ui.profile.ProfileScreen
import com.carenest.presentation.ui.request_service.RequestServiceScreen
import com.carenest.presentation.ui.search_for_nurse.NurseSearchScreen
import com.carenest.presentation.ui.services.details.ServiceDetailsScreen
import com.carenest.presentation.ui.services.list.ServicesScreen
import com.carenest.presentation.ui.home.HomeScreen
import com.carenest.presentation.ui.bookings.BookingsScreen
import com.carenest.presentation.ui.profile.ProfileScreen
import com.carenest.presentation.ui.profile.familymembers.FamilyMembersScreen
import com.carenest.presentation.ui.profile.settings.SettingsScreen
import com.carenest.presentation.ui.home.HomeScreen
import com.carenest.presentation.ui.bookings.BookingsScreen
import com.carenest.presentation.ui.profile.ProfileScreen
import com.carenest.presentation.ui.aichat.choosepatient.ChoosePatientScreen
import com.carenest.presentation.ui.aichat.chat.AIChatScreen
import com.carenest.presentation.ui.aichat.emergency.EmergencyAssistanceScreen
import com.carenest.presentation.model.HealthcareServiceUiModel
import com.carenest.presentation.ui.tracking.NurseOnTheWayScreen
import com.carenest.presentation.ui.visit_summary.VisitCompletedScreen
import com.carenest.presentation.ui.wallet.AddFundsScreen
import com.carenest.presentation.ui.wallet.AddPaymentMethodScreen
import com.carenest.presentation.ui.wallet.WalletScreen
import kotlinx.coroutines.launch
import com.carenest.designsystem.R as RD

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNav(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainState by mainViewModel.state.collectAsState()

    SpTheme(
        isDarkTheme = mainState.isDarkMode,
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

        val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
            /**
             * Clears the current navigation back stack and starts a new navigation flow from the provided destination.
             */
            fun replaceWith(route: NavKey) {
                Snapshot.withMutableSnapshot {
                    backStack.clear()
                    backStack.add(route)
                }
            }

            fun navigateBackOrToHome() {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                } else {
                    val current = backStack.lastOrNull()
                    if (current != null && current !in listOf(AppRoute.Home, AppRoute.Splash, AppRoute.OnBoarding, AppRoute.Login)) {
                        replaceWith(AppRoute.Home)
                    }
                }
            }

            entry<AppRoute.Splash> {
                SplashScreen(
                    onNavigateToOnBoarding = {
                        replaceWith(AppRoute.OnBoarding)
                    },
                    onNavigateToLogin = {
                        replaceWith(AppRoute.Login)
                    },
                    onNavigateToHome = {
                        replaceWith(AppRoute.Home)
                    }
                )
            }

            entry<AppRoute.Login> {
                LoginScreen(
                    onNavigateToOtp = { phone, otp, method ->
                        backStack.add(
                            AppRoute.Otp(
                                phone = phone,
                                otp = otp,
                                method = method
                            )
                        )
                    })
            }

            entry<AppRoute.Otp> { route ->
                OtpScreen(entry = route, onVerificationSuccess = {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(AppRoute.Register)
                    }
                }, onNavigateToRegister = {
                    backStack.add(AppRoute.Register)
                }, onNavigateBack = { navigateBackOrToHome() })
            }
            entry<AppRoute.Register> {
                RegisterScreen(
                    onNavigateBack = { navigateBackOrToHome() },
                    onNavigateToWelcome = {
                        replaceWith(AppRoute.ProfileCompletion)
                    },
                    onNavigateHome = {
                        replaceWith(AppRoute.Home)
                    }
                )
            }

            entry<AppRoute.ProfileCompletion> {
                ProfileCompletionScreen(
                    onNavigateBack = { navigateBackOrToHome() },
                    onNavigateToHome = { replaceWith(AppRoute.Home) })
            }

            entry<AppRoute.Home> {
                HomeScreen(onNavigateToServices = {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(AppRoute.Services)
                    }
                }, onNavigateToBookings = {
                    Snapshot.withMutableSnapshot {
                        backStack.clear()
                        backStack.add(AppRoute.Bookings)
                    }
                }, onNavigateToAIChat = {
                    backStack.add(AppRoute.ChoosePatient)
                }, onNavigateToServiceDetails = { service ->
                    backStack.add(AppRoute.ServiceDetails(service))
                })
            }

            entry<AppRoute.Services> {
                ServicesScreen(
                    onNavigateToDetails = { service ->
                        backStack.add(AppRoute.ServiceDetails(service))
                    },
                )
            }

            entry<AppRoute.ServiceDetails> { route ->
                ServiceDetailsScreen(
                    service = route.service,
                    onNavigateBack = { navigateBackOrToHome() },
                    onRequestService = { service ->
                        backStack.add(AppRoute.RequestService(service = service))
                    })
            }

            entry<AppRoute.OnBoarding> {
                OnBoardingScreen(
                    onNavigateToHome = {
                        replaceWith(AppRoute.Login)
                    },
                )
            }

            entry<AppRoute.Bookings> {
                BookingsScreen()
            }

            entry<AppRoute.Profile> {
                ProfileScreen(
                    onNavigateToFamilyMembers = {
                        backStack.add(AppRoute.FamilyMembers)
                    },
                    onNavigateToHealthProfile = {
                        backStack.add(AppRoute.ProfileCompletion)
                    },
                    onNavigateToSettings = {
                        backStack.add(AppRoute.Settings)
                    },
                    onLogout = {
                        Snapshot.withMutableSnapshot {
                            backStack.clear()
                            backStack.add(AppRoute.Login)
                        }
                    }
                )
            }

            entry<AppRoute.FamilyMembers> {
                FamilyMembersScreen(
                    onNavigateBack = { navigateBackOrToHome() },
                    onNavigateToAddMember = {
                        backStack.add(AppRoute.ChoosePatient)
                    }
                )
            }

            entry<AppRoute.Settings> {
                SettingsScreen(
                    onNavigateBack = { navigateBackOrToHome() }
                )
            }

            entry<AppRoute.Wallet> {
                ScreenTopBar(
                    title = stringResource(R.string.wallet_title),
                    showLeadingIcon = true,
                    onLeadingClick = { if (backStack.size > 1) backStack.removeLastOrNull() },
                )
                WalletScreen(
                    onAddFunds = { backStack.add(AppRoute.AddFunds) },
                    onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) },
                )
            }

            entry<AppRoute.AddFunds> {
                ScreenTopBar(
                    title = stringResource(R.string.wallet_add_funds),
                    onLeadingClick = { if (backStack.size > 1) backStack.removeLastOrNull() },
                )
                AddFundsScreen(
                    onAddPaymentMethod = { backStack.add(AppRoute.AddPaymentMethod) },
                    onTermsClick = {},
                    onAddFunds = {},
                )
            }

            entry<AppRoute.AddPaymentMethod> {
                ScreenTopBar(
                    title = stringResource(R.string.wallet_add_payment_method_title),
                    onLeadingClick = { if (backStack.size > 1) backStack.removeLastOrNull() },
                )
                AddPaymentMethodScreen({}, {}, {}, {}, {})
            }

            entry<AppRoute.NurseOnTheWay> { route ->
                NurseOnTheWayScreen(
                    requestId = route.requestId,
                    onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    onNavigateToQrCode = { },
                    onOpenChat = { nurseId -> },
                    showSnackbar = onShowSnackbar
                )
            }

            entry<AppRoute.RequestService> { route ->
                RequestServiceScreen(
                    onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    onNavigateToMap = { backStack.add(AppRoute.Map) },
                    onNavigateToEditProfile = { /* TODO */ },
                    onNavigateToAddPatient = { /* TODO */ },
                    onNavigateToServiceSelection = { backStack.add(AppRoute.Services) },
                    onNavigateToAddressPicker = { /* TODO */ },
                    onSubmitRequestClick = {
                        backStack.add(AppRoute.SearchForNurse)
                    },
                    selectedService = route.service,
                    mapResultLocation = mapResultLocation,
                    onMapResultConsumed = { mapResultLocation = null },
                )
            }

            entry<AppRoute.Map> {
                MapScreen(
                    onLocationConfirmed = { locationDetails ->
                        mapResultLocation = locationDetails
                        if (backStack.size > 1) backStack.removeLastOrNull()
                    },
                    onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                )
            }

            entry<AppRoute.SearchForNurse> {
                NurseSearchScreen(
                    onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    onMatched = { nurseId ->
                        replaceWith(AppRoute.AcceptOffer)
                    })
            }

            entry<AppRoute.ChoosePatient> {
                ChoosePatientScreen(
                    onNavigateBack = { navigateBackOrToHome() },
                    onNavigateToChat = { patientId ->
                        backStack.add(AppRoute.AIChat(patientId))
                    }
                )
            }

            entry<AppRoute.AIChat> { route ->
                AIChatScreen(
                    patientId = route.patientId,
                    onNavigateBack = { navigateBackOrToHome() },
                    onNavigateToBookings = {
                        Snapshot.withMutableSnapshot {
                            backStack.clear()
                            backStack.add(AppRoute.Bookings)
                        }
                    },
                    onNavigateToServiceDetails = { categoryStr ->
                        val uiModel = HealthcareServiceUiModel(
                            id = categoryStr,
                            name = categoryStr.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            estimatedDurationMinutes = 45L,
                            basePrice = 50.0,
                            description = "Professional care service tailored to your needs.",
                            iconResName = ""
                        )
                        backStack.add(AppRoute.ServiceDetails(uiModel))
                    }
                )
            }

            entry<AppRoute.VisitCompleted> { route ->
                VisitCompletedScreen(
                    requestId = route.requestId, onNavigateHome = {
                        replaceWith(AppRoute.Home)
                    }, onShowSnackbar = onShowSnackbar
                )
            }

            entry<AppRoute.Chat> { route ->
                ChatScreen(
                    onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    requestId = route.requestId,
                    showSnackbar = onShowSnackbar
                )
            }

            entry<AppRoute.EmergencyAssistance> { route ->
                EmergencyAssistanceScreen(
                    onNavigateBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
                    onDismiss = { if (backStack.size > 1) backStack.removeLastOrNull() }
                )
            }
        }

            val bottomNavRoutes = remember {
                listOf<AppRoute>(
                    AppRoute.Home,
                    AppRoute.Services,
                    AppRoute.Bookings,
                    AppRoute.Profile,
                    AppRoute.Wallet,
                )
            }

            val currentRoute = backStack.lastOrNull()
            val selectedIndex = bottomNavRoutes.indexOf(currentRoute)
            val shouldShowBottomBar = currentRoute in bottomNavRoutes

            val shouldHandleBackToHome = currentRoute != null && currentRoute !in listOf(
            AppRoute.Home,
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

            if (currentRoute != targetRoute) {
                Snapshot.withMutableSnapshot {
                    backStack.clear()
                    backStack.add(targetRoute)
                }
            }
        }

        val topBarState = remember { mutableStateOf(TopBarConfiguration()) }

        CompositionLocalProvider(LocalTopBarState provides topBarState) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
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
                                config.leadingIcon
                                    ?: painterResource(id = RD.drawable.ic_arrow_back)
                            } else null,
                            onLeadingClick = config.onLeadingClick,
                            autoMirrorLeadingIcon = true,
                            actions = buildList {
                                if (config.profileImage != null) {
                                    add(
                                        TopBarAction(
                                            config.profileImage,
                                            "Profile",
                                            onClick = config.onProfileClick ?: {})
                                    )
                                }
                                config.trailingAction?.let(::add)
                            },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }
                }
            ) { paddingValues ->

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavDisplay<NavKey>(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = paddingValues.calculateTopPadding())
                            .then(
                                if (shouldShowBottomBar) Modifier
                                else Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                            ),
                        entries = rememberDecoratedNavEntries(
                            backStack = backStack,
                            entryProvider = entryProvider,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            )
                        ),
                        onBack = {
                            if (backStack.size > 1) {
                                backStack.removeLastOrNull()
                            } else {
                                Snapshot.withMutableSnapshot {
                                    backStack.clear()
                                    backStack.add(AppRoute.Home)
                                }
                            }
                        },
                    )

                    if (shouldShowBottomBar) {
                        SPBottomNavigation(
                            items = listOf(
                                BottomNavItem(
                                    stringResource(R.string.nav_home),
                                    RD.drawable.ic_bottom_nav_home
                                ), BottomNavItem(
                                    stringResource(R.string.nav_services),
                                    RD.drawable.ic_bottom_nav_services
                                ), BottomNavItem(
                                    stringResource(R.string.nav_booking),
                                    RD.drawable.ic_bottom_nav_bookings
                                ), BottomNavItem(
                                    stringResource(R.string.nav_profile),
                                    RD.drawable.ic_bottom_nav_profile
                                ), BottomNavItem(
                                    stringResource(R.string.wallet_title),
                                    RD.drawable.ic_bottom_nav_wallet
                                )
                            ),
                            selectedIndex = if (selectedIndex != -1) selectedIndex else 0,
                            onItemSelected = { index -> onBottomNavItemSelected(index) },
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
