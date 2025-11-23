// commonMain/kotlin/.../SharedModule.kt
val sharedModule = module {
    // Koin finds the correct 'SettingsFactory' from the platform modules above
    single { AuthRepository(settingsFactory = get()) }

    // Provide the ViewModel
    factory { LoginViewModel(authRepository = get(), client = get()) }
}
