package com.phanduy.tekotest.di

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.phanduy.tekotest.data.repository.ProductApiRepository
import com.phanduy.tekotest.data.repository.ProductCartRepository
import com.phanduy.tekotest.factory.ProductDetailViewModelFactory
import com.phanduy.tekotest.viewmodel.GamingViewModel
import com.phanduy.tekotest.viewmodel.KatakanaViewModel
import com.phanduy.tekotest.viewmodel.LearningViewModel
import com.phanduy.tekotest.viewmodel.ProductDetailViewModel
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

@Reusable
class InjectingSavedStateViewModelFactory @Inject constructor(
        private val assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
        private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    /**
     * Creates instance of ViewModel either annotated with @AssistedInject or @Inject and passes dependencies it needs.
     */
    fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                override fun <T : ViewModel?> create(
                        key: String,
                        modelClass: Class<T>,
                        handle: SavedStateHandle
                ): T {
                    val viewModel =
                            createAssistedInjectViewModel(modelClass, handle)
                                    ?: createInjectViewModel(modelClass)
                                    ?: throw IllegalArgumentException("Unknown model class $modelClass")

                    try {
                        @Suppress("UNCHECKED_CAST")
                        return viewModel as T
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }
            }

    fun createProductDetailFactory(sku: String, productApiRepository: ProductApiRepository,
               productCartRepository: ProductCartRepository, owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                override fun <T : ViewModel?> create(
                        key: String,
                        modelClass: Class<T>,
                        handle: SavedStateHandle
                ): T {
                    val viewModel =
                            ProductDetailViewModel(productApiRepository, productCartRepository, sku, handle) as T

                    try {
                        @Suppress("UNCHECKED_CAST")
                        return viewModel as T
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }
            }

    fun createLearningFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModel =
                    LearningViewModel(handle) as T

                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

    fun createKatakanaFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModel =
                    KatakanaViewModel(handle) as T

                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

    fun createGamingFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModel =
                    GamingViewModel(handle) as T

                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

    /**
     * Creates ViewModel based on @AssistedInject constructor and its factory
     */
    private fun <T : ViewModel?> createAssistedInjectViewModel(
            modelClass: Class<T>,
            handle: SavedStateHandle
    ): ViewModel? {
        val creator = assistedFactories[modelClass]
                ?: assistedFactories.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
                ?: return null

        return creator.create(handle)
    }

    /**
     * Creates ViewModel based on regular Dagger @Inject constructor
     */
    private fun <T : ViewModel?> createInjectViewModel(modelClass: Class<T>): ViewModel? {
        val creator = viewModelProviders[modelClass]
                ?: viewModelProviders.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
                ?: return null

        return creator.get()
    }
}