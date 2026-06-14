/*ArkhamCalc
Copyright (C) 2012  Matthew Cole

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/
package com.eitanadler.arkhamcalcredux2

import android.app.Activity
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.View
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter

/**
 * An activity that displays help topics and content. Strings are stored in the
 * res folder. The nth entry in the 'topic' String array is associated with
 * the nth entry in the 'content' String array.
 *
 * Much of this code comes from ExpandableListActivity. Copied to avoid deprecation and then cleaned up to avoid keeping copied generic code. Future work to fix this.
 */
public class ArkhamCalcHelp : Activity(), View.OnCreateContextMenuListener,
    ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener,
    ExpandableListView.OnGroupExpandListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topics = getHelp(R.array.topics)
        val contents = getHelp(R.array.contents)

        val adapter: ExpandableListAdapter = SimpleExpandableListAdapter(
            this,
            getGroupData(topics),
            R.layout.help_group_row,
            arrayOf("helpTopic"),
            intArrayOf(R.id.helpTopicTextView),
            getChildData(contents),
            R.layout.help_child_row,
            arrayOf("helpContent"),
            intArrayOf(R.id.helpContentTextView)
        )
        setListAdapter(adapter)


        //if this activity was started with the extra BUNDLE_TOPIC, expand and focus
        //on the passed in topic. Assumes that the topic passed in is part of the
        //help.xml 'topic' String array.
        val bundleTopic = intent.getCharSequenceExtra(BUNDLE_TOPIC)
        if (bundleTopic != null) {
            val topicIndex = topics.indexOf(bundleTopic.toString())
            val view = findViewById<ExpandableListView>(android.R.id.list)
            view.expandGroup(topicIndex)
            view.setSelectionFromTop(topicIndex, 0)
        }
    }

    private fun getHelp(helpId: Int): List<String> {
        val helpStringArray = resources.getStringArray(helpId)
        return listOf(*helpStringArray)
    }

    internal companion object {
        const val BUNDLE_TOPIC: String = "BUNDLE_TOPIC"

        internal fun getGroupData(topics: Iterable<String>): List<Map<String, String>> {
            val groupList: MutableList<MutableMap<String, String>> =
                ArrayList()

            for (topic in topics) {
                val groupMap: MutableMap<String, String> = HashMap()
                groupMap["helpTopic"] = topic
                groupList.add(groupMap)
            }

            return groupList
        }

        private fun getChildData(contents: Iterable<String>): List<List<Map<String, String>>> {
            val childrenList: MutableList<MutableList<MutableMap<String, String>>> =
                ArrayList()

            for (content in contents) {
                val childList: MutableList<MutableMap<String, String>> =
                    ArrayList()
                val childMap: MutableMap<String, String> = HashMap()
                childMap["helpContent"] = content
                childList.add(childMap)
                childrenList.add(childList)
            }

            return childrenList
        }
    }

    private var mAdapter: ExpandableListAdapter? = null
    private var mList: ExpandableListView? = null
    private var mFinishedStart: Boolean = false

    /**
     * Override this to populate the context menu when an item is long pressed. menuInfo
     * will contain an [ExpandableListView.ExpandableListContextMenuInfo]
     * whose packedPosition is a packed position
     * that should be used with [ExpandableListView.getPackedPositionType] and
     * the other similar methods.
     *
     *
     * {@inheritDoc}
     */
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
    }

    /**
     * Override this for receiving callbacks when a child has been clicked.
     *
     *
     * {@inheritDoc}
     */
    override fun onChildClick(
        parent: ExpandableListView?, v: View?, groupPosition: Int,
        childPosition: Int, id: Long
    ): Boolean {
        return false
    }

    /**
     * Override this for receiving callbacks when a group has been collapsed.
     */
    override fun onGroupCollapse(groupPosition: Int) {
    }

    /**
     * Override this for receiving callbacks when a group has been expanded.
     */
    override fun onGroupExpand(groupPosition: Int) {
    }

    /**
     * Ensures the expandable list view has been created before Activity restores all
     * of the view states.
     *
     * @see Activity.onRestoreInstanceState
     */
    override fun onRestoreInstanceState(state: Bundle) {
        ensureList()
        super.onRestoreInstanceState(state)
    }

    /**
     * Updates the screen state (current list and other views) when the
     * content changes.
     *
     * @see Activity.onContentChanged
     */
    override fun onContentChanged() {
        super.onContentChanged()
        val emptyView = findViewById<View?>(android.R.id.empty)
        mList = findViewById<View?>(android.R.id.list) as ExpandableListView?
        if (mList == null) {
            throw RuntimeException(
                "Your content must have a ExpandableListView whose id attribute is " +
                        "'android.R.id.list'"
            )
        }
        if (emptyView != null) {
            mList!!.setEmptyView(emptyView)
        }
        mList!!.setOnChildClickListener(this)
        mList!!.setOnGroupExpandListener(this)
        mList!!.setOnGroupCollapseListener(this)

        if (mFinishedStart) {
            setListAdapter(mAdapter)
        }
        mFinishedStart = true
    }

    /**
     * Provide the adapter for the expandable list.
     */
    private fun setListAdapter(adapter: ExpandableListAdapter?) {
        synchronized(this) {
            ensureList()
            mAdapter = adapter
            mList!!.setAdapter(adapter)
        }
    }

    private fun ensureList() {
        if (mList != null) {
            return
        }
        setContentView(android.R.layout.expandable_list_content)
    }

}
