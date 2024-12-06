package com.example.arcore_playground

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }

        // Initialize AR Fragment
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        // Set up tap listener for placing objects
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, _, _ ->
            placeObject(hitResult)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            // Permission denied, show a message or handle the error
            Toast.makeText(this, "Camera permission is required for AR", Toast.LENGTH_LONG).show()
        }
    }

    private fun placeObject(hitResult: HitResult) {
        // Load the 3D model
        ModelRenderable.builder()
            .setSource(this, Uri.parse("raw/chicken.glb")) // Replace with your 3D model file
            .build()
            .thenAccept { modelRenderer ->
                // Create an anchor for the object
                val anchorNode = AnchorNode(hitResult.createAnchor())
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Add a transformable node for interaction
                val transformableNode = TransformableNode(arFragment.transformationSystem)
                transformableNode.setParent(anchorNode)
                transformableNode.renderable = modelRenderer
                transformableNode.select()
            }
            .exceptionally { throwable ->
                Toast.makeText(
                    this,
                    "Failed to load model: ${throwable.message}",
                    Toast.LENGTH_LONG
                ).show()
                null
            }
    }
}
