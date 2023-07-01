package github.sachin2dehury.locationdemo

import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object LocationServiceModule {

    @Provides
    @ServiceScoped
    fun provideLocationProvider(@ApplicationContext context: Context): LocationProvider =
        LocationProviderImpl(context, LocationServices.getFusedLocationProviderClient(context))
}