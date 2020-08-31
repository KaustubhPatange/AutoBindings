package com.kpstv.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.kpstv.lint.detectors.RecyclerViewDetector

class IssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

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
            RecyclerViewDetector.ISSUE_INCORRECT_ITEMVIEWTYPE
        )
}