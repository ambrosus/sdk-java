package com.ambrosus.ambrosussdk.utils

import java.io.Serializable

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

data class Section(val sectionName: String, var items: Map<String, Any>) : Serializable

internal class SectionFormatter {
    companion object {
        val contentKey = "content"

        fun fetchAdditionalSections(map: MutableMap<String, Any>): List<Section> {
            val additionalSections = getSections(map)
            additionalSections.forEach {
                map.remove(it.sectionName)
            }

            return additionalSections
        }

        fun getSections(data: Map<String, Any>): List<Section> {
            val formattedData = data.toMutableMap()
            val sections: ArrayList<Section> = ArrayList()

            val keys = formattedData.keys.toList()
            for (key in keys) {
                val dictionary = formattedData[key]
                val isMap = dictionary is Map<*, *>
                val isListOfMaps = dictionary is List<*>
                if (isMap) {
                    val dict = dictionary as MutableMap<String, Any>
                    formattedData.remove(key)
                    sections.addAll(fetchAdditionalSections(map = dict))
                    val dataSection = Section(sectionName = key, items =
                    dict)
                    sections.add(dataSection)
                } else if (isListOfMaps) {
                    val dictionaries = dictionary as List<Any>
                    if (dictionaries.all {
                                it is MutableMap<*,
                                        *>
                            }) {
                        for ((i, dictionary) in dictionaries.withIndex()) {
                            val dictInternal = dictionary as MutableMap<String, Any>
                            // index the dictionaries that belong to the same parent
                            sections.addAll(fetchAdditionalSections(dictInternal))
                            val keyIndex = if (i > 0) key + " ${i + 1}" else key
                            val dataSection = Section(sectionName = keyIndex,
                                    items = dictInternal)
                            sections.add(dataSection)
                        }
                    }
                }
            }

            return sections
        }


        fun getFormattedSections(data: Map<String, Any>): List<Section> {
            val formattedData = data.toMutableMap()

            data.forEach {
                if (it.value is Map<*, *> || it.value is
                                List<*>) {
                    formattedData.remove(it.key)
                }
            }

            val sections = getSections(data).toMutableList()

            for ((i, section) in sections.withIndex()) {
                val dictionary = section.items[contentKey] as? MutableMap<String, Any>
                if (dictionary != null) {
                    for (key in formattedData.keys) {
                        formattedData[key]?.let { dictionary.put(key, it) }
                    }
                    sections.removeAt(i)
                    sections.add(Section(sectionName = contentKey, items
                    = dictionary))
                }
            }

            return sections.filter { it.items.size > 0 }
        }
    }
}