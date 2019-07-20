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

package com.eitanadler.arkhamcalcredux2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ExpandableListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

/**
 * An activity that displays help topics and content. Strings are stored in the
 * res folder. The nth entry in the 'topic' String array is associated with
 * the nth entry in the 'content' String array.
 */
public class ArkhamCalcHelp extends ExpandableListActivity
{
    public static final String BUNDLE_TOPIC = "BUNDLE_TOPIC";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        List<String> topics = getHelp(R.array.topics);
        List<String> contents = getHelp(R.array.contents);
        
        ExpandableListAdapter adapter = new SimpleExpandableListAdapter
            (this, getGroupData(topics), R.layout.help_group_row, 
             new String[] { "helpTopic" }, new int[] { R.id.helpTopicTextView }, 
             getChildData(contents), R.layout.help_child_row,
             new String[] { "helpContent" }, new int[] {R.id.helpContentTextView});
        setListAdapter(adapter);
       
        //if this activity was started with the extra BUNDLE_TOPIC, expand and focus
        //on the passed in topic. Assumes that the topic passed in is part of the
        //help.xml 'topic' String array.
        CharSequence bundleTopic = getIntent().getCharSequenceExtra(BUNDLE_TOPIC);
        if (bundleTopic != null) {
            int topicIndex = topics.indexOf(bundleTopic.toString());
            ExpandableListView view = (ExpandableListView)findViewById(android.R.id.list);
            view.expandGroup(topicIndex);
            view.setSelectionFromTop(topicIndex, 0);
        }
    }
    
    private List<String> getHelp(int helpId)
    {
        Resources res = getResources();
        String[] helpStringArray = res.getStringArray(helpId);
        return Arrays.asList(helpStringArray);
    }
    
    private static List<Map<String, String>> getGroupData(List<String> topics)
    {
        List<Map<String, String>> groupList = new ArrayList<Map<String, String>>();
        
        for (String topic : topics) {
            Map<String, String> groupMap = new HashMap<String, String>();
            groupMap.put("helpTopic", topic);
            groupList.add(groupMap);            
        }
        
        return groupList;
    }
    
    private static List<List<Map<String, String>>> getChildData(List<String> contents)
    {
        List<List<Map<String, String>>> childrenList = new ArrayList<List<Map<String, String>>>();
        
        for (String content : contents) {
            List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
            Map<String, String> childMap = new HashMap<String, String>();
            childMap.put("helpContent", content);
            childList.add(childMap);
            childrenList.add(childList);            
        }
        
        return childrenList;
    }
}
