package com.carenest.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD
import com.carenest.presentation.navigation.NavigationConfig.savedStateConfiguration
import kotlin.collections.listOf

@Composable
fun AppNav() {

    val initialRoute: NavKey = AppRoute.Splash

    val backStack = rememberNavBackStack(
        savedStateConfiguration,
        initialRoute
    )

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {

        entry<AppRoute.Splash> {
            // TODO: Add SplashScreen
        }

    }

    /**
     * Main destinations that will appear in the Bottom Navigation.
     * Add routes here after creating the main application screens.
     */
    val bottomNavRoutes = remember {
        listOf<AppRoute>(
            // AppRoute.Home,
            // AppRoute.Bookings,
            // AppRoute.Profile
        )
    }

    val currentRoute = backStack.lastOrNull()
    val selectedIndex = bottomNavRoutes.indexOf(currentRoute)
    val shouldShowBottomBar = currentRoute in bottomNavRoutes

    fun onBottomNavItemSelected(index: Int) {
        val targetRoute = bottomNavRoutes[index]

        if (currentRoute != targetRoute) {
            Snapshot.withMutableSnapshot {
                backStack.clear()
                backStack.add(targetRoute)
            }
        }
    }

    /**
     * Clears the current navigation back stack and starts a new navigation flow from the provided destination.
     *
     * Common use cases:
     * - After successful login
     * - After completing onboarding
     * - After logout
     * - When resetting the app flow
     */
    fun replaceWith(route: NavKey) {
        Snapshot.withMutableSnapshot {
            backStack.clear()
            backStack.add(route)
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            NavDisplay<NavKey>(
                modifier = Modifier
                    .fillMaxSize()
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
                onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            )

            if (shouldShowBottomBar) {
                SPBottomNavigation(
                    items = listOf(
                        BottomNavItem(
                            stringResource(R.string.nav_home),
                            RD.drawable.ic_home
                        ),
                        BottomNavItem(
                            stringResource(R.string.nav_booking),
                            RD.drawable.ic_booking
                        ),
                        BottomNavItem(
                            stringResource(R.string.nav_services),
                            RD.drawable.ic_services
                        ),
                        BottomNavItem(
                            stringResource(R.string.nav_profile),
                            RD.drawable.ic_profile
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