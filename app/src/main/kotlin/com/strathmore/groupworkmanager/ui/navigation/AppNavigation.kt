package com.strathmore.groupworkmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strathmore.groupworkmanager.di.AppContainer
import com.strathmore.groupworkmanager.ui.screens.AddTaskScreen
import com.strathmore.groupworkmanager.ui.screens.CommentsScreen
import com.strathmore.groupworkmanager.ui.screens.GroupCreationScreen
import com.strathmore.groupworkmanager.ui.screens.GroupDetailScreen
import com.strathmore.groupworkmanager.ui.screens.HomeScreen
import com.strathmore.groupworkmanager.ui.screens.OnboardingScreen
import com.strathmore.groupworkmanager.ui.viewmodel.GroupDetailViewModel
import com.strathmore.groupworkmanager.ui.viewmodel.HomeViewModel
import com.strathmore.groupworkmanager.ui.viewmodel.OnboardingViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Defines the navigation graph for the app. When adding new screens
 * remember to register them here with a unique route.
 */
@Composable
fun AppNavigation(appContainer: AppContainer, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            val vm: OnboardingViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return OnboardingViewModel(appContainer.userRepository) as T
                }
            })
            OnboardingScreen(viewModel = vm, onContinue = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("home") {
            val vm: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(
                        appContainer.groupRepository,
                        appContainer.taskRepository
                    ) as T
                }
            })
            HomeScreen(
                viewModel = vm,
                onCreateGroup = { navController.navigate("createGroup") },
                onGroupSelected = { groupId -> navController.navigate("groupDetail/$groupId") }
            )
        }
        composable("createGroup") {
            GroupCreationScreen(
                onBack = { navController.popBackStack() },
                onGroupCreated = { newGroupId ->
                    navController.popBackStack()
                    navController.navigate("groupDetail/$newGroupId")
                },
                groupRepository = appContainer.groupRepository
            )
        }
        composable(
            route = "groupDetail/{groupId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable
            val vm: GroupDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return GroupDetailViewModel(
                            savedStateHandle = SavedStateHandle(mapOf("groupId" to groupId)),
                            groupRepository = appContainer.groupRepository,
                            memberRepository = appContainer.memberRepository,
                            taskRepository = appContainer.taskRepository,
                            commentRepository = appContainer.commentRepository
                        ) as T
                    }
                }
            )
            GroupDetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onAddTask = { navController.navigate("addTask/$groupId") },
                onViewComments = { navController.navigate("comments/$groupId") },
                onShareGroup = TODO()
            )
        }
        composable(
            route = "addTask/{groupId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable
            val vm: GroupDetailViewModel = viewModel(
                key = "addTask_$groupId",
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return GroupDetailViewModel(
                            savedStateHandle = SavedStateHandle(mapOf("groupId" to groupId)),
                            groupRepository = appContainer.groupRepository,
                            memberRepository = appContainer.memberRepository,
                            taskRepository = appContainer.taskRepository,
                            commentRepository = appContainer.commentRepository
                        ) as T
                    }
                }
            )
            AddTaskScreen(
                groupId = groupId,
                viewModel = vm,
                onTaskAdded = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "comments/{groupId}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable
            val vm: GroupDetailViewModel = viewModel(
                key = "comments_$groupId",
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return GroupDetailViewModel(
                            savedStateHandle = SavedStateHandle(mapOf("groupId" to groupId)),
                            groupRepository = appContainer.groupRepository,
                            memberRepository = appContainer.memberRepository,
                            taskRepository = appContainer.taskRepository,
                            commentRepository = appContainer.commentRepository
                        ) as T
                    }
                }
            )
            CommentsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "qrShare/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable

            // Create ViewModel to get group details
            val vm: GroupDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return GroupDetailViewModel(
                            savedStateHandle = SavedStateHandle(mapOf("groupId" to groupId)),
                            groupRepository = appContainer.groupRepository,
                            memberRepository = appContainer.memberRepository,
                            taskRepository = appContainer.taskRepository,
                            commentRepository = appContainer.commentRepository
                        ) as T
                    }
                }
            )

            val group by vm.group.collectAsState(initial = null)
        }
    }
}
