package com.driverfinance.service.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * Extracts text and node tree JSON from AccessibilityNodeInfo hierarchy.
 * Used by CaptureAccessibilityService to read Shopee Driver UI.
 *
 * Performance guards:
 * - Max depth: 25 levels
 * - Max nodes: 500 per tree
 */
class AccessibilityNodeExtractor {

    companion object {
        private const val MAX_DEPTH = 25
        private const val MAX_NODES = 500
    }

    fun extractAllText(rootNode: AccessibilityNodeInfo): String {
        val builder = StringBuilder()
        traverseForText(rootNode, builder, 0)
        return builder.toString().trim()
    }

    fun extractNodeTree(rootNode: AccessibilityNodeInfo): String {
        val nodeCount = intArrayOf(0)
        val json = buildNodeJson(rootNode, 0, nodeCount)
        return json.toString()
    }

    private fun traverseForText(
        node: AccessibilityNodeInfo,
        builder: StringBuilder,
        depth: Int
    ) {
        if (depth > MAX_DEPTH) return

        val text = node.text?.toString()
        val contentDesc = node.contentDescription?.toString()

        if (!text.isNullOrBlank()) {
            builder.append(text).append("\n")
        } else if (!contentDesc.isNullOrBlank()) {
            builder.append(contentDesc).append("\n")
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            traverseForText(child, builder, depth + 1)
        }
    }

    private fun buildNodeJson(
        node: AccessibilityNodeInfo,
        depth: Int,
        nodeCount: IntArray
    ): JSONObject {
        nodeCount[0]++

        val rect = Rect()
        node.getBoundsInScreen(rect)

        val json = JSONObject().apply {
            put("depth", depth)
            put("className", node.className?.toString().orEmpty())
            put("text", node.text?.toString().orEmpty())
            put("contentDescription", node.contentDescription?.toString().orEmpty())
            put("resourceId", node.viewIdResourceName.orEmpty())
            put("bounds", "${rect.left},${rect.top},${rect.right},${rect.bottom}")
            put("childCount", node.childCount)
        }

        if (node.childCount > 0 && depth < MAX_DEPTH && nodeCount[0] < MAX_NODES) {
            val children = JSONArray()
            for (i in 0 until node.childCount) {
                if (nodeCount[0] >= MAX_NODES) break
                val child = node.getChild(i) ?: continue
                children.put(buildNodeJson(child, depth + 1, nodeCount))
            }
            json.put("children", children)
        }

        return json
    }
}
