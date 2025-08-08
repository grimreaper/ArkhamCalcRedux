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

import android.app.ExpandableListActivity
import android.os.Bundle
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import java.util.Arrays

/**
 * An activity that displays help topics and content. Strings are stored in the
 * res folder. The nth entry in the 'topic' String array is associated with
 * the nth entry in the 'content' String array.
 */
public class ArkhamCalcHelp : ExpandableListActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val topics = getHelp(R.array.topics)
        val contents = getHelp(R.array.contents)

        val adapter: ExpandableListAdapter = SimpleExpandableListAdapter(
            this, getGroupData(topics), R.layout.help_group_row,
            arrayOf("helpTopic"), intArrayOf(R.id.helpTopicTextView),
            getChildData(contents), R.layout.help_child_row,
            arrayOf("helpContent"), intArrayOf(R.id.helpContentTextView)
        )
        setListAdapter(adapter)


        //if this activity was started with the extra BUNDLE_TOPIC, expand and focus
        //on the passed in topic. Assumes that the topic passed in is part of the
        //help.xml 'topic' String array.
        val bundleTopic = getIntent().getCharSequenceExtra(BUNDLE_TOPIC)
        if (bundleTopic != null) {
            val topicIndex = topics.indexOf(bundleTopic.toString())
            val view = findViewById<ExpandableListView>(android.R.id.list)
            view.expandGroup(topicIndex)
            view.setSelectionFromTop(topicIndex, 0)
        }
    }

    private fun getHelp(helpId: Int): MutableList<String?> {
        val res = getResources()
        val helpStringArray = res.getStringArray(helpId)
        return Arrays.asList<String?>(*helpStringArray)
    }

    internal companion object {
        const val BUNDLE_TOPIC: String = "BUNDLE_TOPIC"

        private fun getGroupData(topics: Iterable<String?>): MutableList<MutableMap<String?, String?>?> {
            val groupList: MutableList<MutableMap<String?, String?>?> =
                ArrayList()

            for (topic in topics) {
                val groupMap: MutableMap<String?, String?> = HashMap()
                groupMap.put("helpTopic", topic)
                groupList.add(groupMap)
            }

            return groupList
        }

        private fun getChildData(contents: Iterable<String?>): MutableList<MutableList<MutableMap<String?, String?>?>?> {
            val childrenList: MutableList<MutableList<MutableMap<String?, String?>?>?> =
                ArrayList()

            for (content in contents) {
                val childList: MutableList<MutableMap<String?, String?>?> =
                    ArrayList()
                val childMap: MutableMap<String?, String?> = HashMap()
                childMap.put("helpContent", content)
                childList.add(childMap)
                childrenList.add(childList)
            }

            return childrenList
        }
    }
}
