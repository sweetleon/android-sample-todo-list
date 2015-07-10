package com.example.test;

import com.example.BuildConfig;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.robolectric.manifest.AndroidManifest;
import retrofit.RestAdapter;

public class MyTestRunner extends RobolectricGradleTestRunner {
    public MyTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        if (config.constants() == Void.class) {
            config = new Config.Implementation(config.sdk(), config.manifest(), config.qualifiers(), config.packageName(), config.resourceDir(), config.assetDir(), config.shadows(), config.application(), config.libraries(), BuildConfig.class);
        }
        return super.getAppManifest(config);
    }

    @Override
    public InstrumentationConfiguration createClassLoaderConfig() {
        return InstrumentationConfiguration.newBuilder()
                .addInstrumentedPackage(RestAdapter.class.getPackage().getName())
                .build();
    }
}
