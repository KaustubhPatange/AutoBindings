package com.kpstv.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.kpstv.lint.detectors.RecyclerViewDetector
import com.kpstv.lint.detectors.TypeConvertDetector

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val minApi: Int
        get() = api

    override val issues: List<Issue>
        get() = listOf(
            RecyclerViewDetector.ISSUE_ON_BIND,
            RecyclerViewDetector.ISSUE_RECYCLERVIEW,
            RecyclerViewDetector.ISSUE_INCORRECT_BIND,
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFCONTENTSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFITEMSAME,
            RecyclerViewDetector.ISSUE_NO_DIFFITEMSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_ONCLICK,
            RecyclerViewDetector.ISSUE_INCORRECT_ONLONGCLICK,
            RecyclerViewDetector.ISSUE_NO_DIFFCONTENTSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_ITEMVIEWTYPE,
            TypeConvertDetector.ISSUE_NO_SERIALIZABLE,
            TypeConvertDetector.ISSUE_NO_JSONCLASS,
            TypeConvertDetector.ISSUE_NO_INTERFACE,
            TypeConvertDetector.ISSUE_WRONG_RETURN_TYPE
        )

    override val vendor: Vendor = Vendor(
        vendorName = "Kaustubh Patange",
        feedbackUrl = "https://github.com/KaustubhPatange/AutoBindings/issues",
        contact = "https://github.com/KaustubhPatange/AutoBindings"
    )
}