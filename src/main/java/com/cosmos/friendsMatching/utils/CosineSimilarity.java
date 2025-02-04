package com.cosmos.friendsMatching.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.Map;


@Component
public class CosineSimilarity {

    // 计算两个字符串列表的余弦相似度
    public static double cosine(Set<String> list1, Set<String> list2) {
        Map<String, Integer> vectorA = getFrequencyVector(list1);
        Map<String, Integer> vectorB = getFrequencyVector(list2);

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String key : vectorA.keySet()) {
            int freqA = vectorA.get(key);
            dotProduct += freqA * vectorB.getOrDefault(key, 0);
            normA += Math.pow(freqA, 2);
        }

        for (int freqB : vectorB.values()) {
            normB += Math.pow(freqB, 2);
        }

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (normA * normB);
    }

    // 计算字符串列表的词频向量
    private static Map<String, Integer> getFrequencyVector(Set<String> list) {
        Map<String, Integer> freqMap = new HashMap<>();

        for (String text : list) {
            String[] words = text.toLowerCase().split("\\W+");
            for (String word : words) {
                freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
            }
        }

        return freqMap;
    }

    public static void main(String[] args) {
        Set<String> list1 = Set.of("java", "python", "C++");

        Set<String> list2 = Set.of("java", "python");

        double similarity = cosine(list1, list2);
        System.out.println("余弦相似度: " + similarity);
    }
}