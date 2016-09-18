/*
 * Copyright (C) 2016 Muhammad Tayyab Akram
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mta.tehreer.internal.util;

import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class LruCache {

    private static class Node<K, V> {

        public final Segment<K, V> segment;
        public final K key;
        public V value;
        public Node<K, V> previous;
        public Node<K, V> next;

        public Node(Segment<K, V> segment, K key, V value) {
            this.segment = segment;
            this.key = key;
            this.value = value;
        }
    }

    private static class List {

        Node header;

        public List() {
            header = new Node(null, null, null);
            header.previous = header.next = header;
        }

        public Node last() {
            return header.previous;
        }

        public void makeFirst(Node node) {
            remove(node);
            addFirst(node);
        }

        public void addFirst(Node node) {
            node.previous = header;
            node.next = header.next;
            header.next.previous = node;
            header.next = node;
        }

        public void remove(Node node) {
            node.previous.next = node.next;
            node.next.previous = node.previous;
            node.next = node.previous = null;
        }

        public void clear() {
            header.previous = header.next = header;
        }
    }

    protected static class Segment<K, V> {

        protected final LruCache cache;
        private HashMap<K, Node<K, V>> map;

        public Segment(LruCache cache) {
            if (cache == null) {
                throw new NullPointerException();
            }

            this.cache = cache;
            this.map = new HashMap<>();
        }

        protected int sizeOf(K key, V value) {
            return 1;
        }

        public final V get(K key) {
            synchronized (cache) {
                Node<K, V> node = map.get(key);
                if (node != null) {
                    cache.list.makeFirst(node);
                    return node.value;
                }
            }

            return null;
        }

        public final void put(K key, V value) {
            synchronized (cache) {
                Node<K, V> newNode = new Node<>(this, key, value);
                Node<K, V> oldNode = map.put(key, newNode);
                if (oldNode != null) {
                    throw new IllegalArgumentException("An entry with same key has already been added");
                }

                cache.size += sizeOf(key, value);
                cache.list.addFirst(newNode);
            }

            cache.trimToSize(cache.capacity);
        }

        public final void remove(K key) {
            synchronized (cache) {
                Node<K, V> node = map.remove(key);
                if (node != null) {
                    cache.size -= sizeOf(key, node.value);
                    cache.list.remove(node);
                }
            }
        }
    }

    private List list;
    private int capacity;
    private int size;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid Capacity: " + capacity);
        }

        this.list = new List();
        this.capacity = capacity;
        this.size = 0;
    }

    public synchronized final int capacity() {
        return capacity;
    }

    public synchronized final int size() {
        return size;
    }

    public synchronized void clear() {
        list.clear();
    }

    public void trimToSize(int maxSize) {
        while (true) {
            synchronized (this) {
                if (size <= maxSize) {
                    break;
                }

                Node toEvict = list.last();
                if (toEvict == list.header) {
                    break;
                }

                Segment segment = toEvict.segment;
                Object key = toEvict.key;
                segment.remove(key);
            }
        }
    }
}
