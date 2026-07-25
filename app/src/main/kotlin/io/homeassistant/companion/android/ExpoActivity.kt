package io.homeassistant.companion.android

import android.os.Bundle
import io.homeassistant.brownfield.BrownfieldActivity
import io.homeassistant.brownfield.showReactNativeFragment

/**
 * Hosts the React Native screen shipped by the Expo brownfield AAR
 * (`io.homeassistant.brownfield:habrownfield-fused-release`).
 *
 * [BrownfieldActivity] extends AppCompatActivity and forwards configuration
 * changes; [showReactNativeFragment] mounts the RN root fragment (module "main")
 * and wires native back-button handling.
 */
class ExpoActivity : BrownfieldActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showReactNativeFragment()
    }
}
