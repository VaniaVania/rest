package com.ivan.restapplication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ivan.restapplication.api.ApiBinding;
import com.ivan.restapplication.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class UserService extends ApiBinding implements SpotifyUserService {

    @Autowired
    private ObjectMapper mapper;

    public UserService(String accessToken) {
        super(accessToken);
    }

    @Override
    public JsonNode showUserProfile() throws JsonProcessingException {
        return mapper
                .readTree(restTemplate
                        .getForObject("https://api.spotify.com/v1/me", String.class));
    }

    @Override
    public UserDTO getSpotifyUserDTO() {
        return restTemplate.getForObject("https://api.spotify.com/v1/me", UserDTO.class);
    }

    @Override
    public JsonNode findTopArtists(String term) throws JsonProcessingException {
        return mapper
                .readTree(restTemplate
                        .getForObject("https://api.spotify.com/v1/me/top/artists?limit=50&time_range=" + term, String.class)).get("items");
    }

    @Override
    public JsonNode findTopTracks(String term) throws JsonProcessingException {
        return mapper
                .readTree(restTemplate
                        .getForObject("https://api.spotify.com/v1/me/top/tracks?limit=50&time_range=" + term, String.class)).get("items");
    }

    @Override
    public JsonNode getFollowedArtists() throws JsonProcessingException {
        JsonNode followedArtistsJson = mapper
                .readTree(restTemplate
                        .getForObject("https://api.spotify.com/v1/me/following?type=artist&limit=50", String.class)
                );

        ArrayNode items = (ArrayNode) followedArtistsJson.findValue("items");

        String next = followedArtistsJson.get("artists").get("next").asText();

        while (!next.equals("null")) {
            JsonNode loopFollowedArtistsJson = mapper
                    .readTree(restTemplate
                            .getForObject(next, String.class)
                    );
            for (JsonNode e : loopFollowedArtistsJson.findValue("items")) {
                items.add(e);
            }
            next = loopFollowedArtistsJson.findValue("next").asText();
        }

        List<JsonNode> sortedDataNodes = items.findParents("name")
                .stream()
                .sorted(Comparator.comparing(o -> o.get("name").asText()))
                .collect(Collectors.toList());
        //create ArrayNode
        return mapper.createObjectNode().arrayNode().addAll(sortedDataNodes);  //TODO
    }

    @Override
    public void unfollowArtists(String ids) {
        restTemplate.delete("https://api.spotify.com/v1/me/following?type=artist&ids=" + ids, HttpMethod.DELETE);
    }

    @Override
    public void followArtists(String ids) {
        restTemplate.put("https://api.spotify.com/v1/me/following?type=artist&ids=" + ids, HttpMethod.PUT);
    }

    @Override
    public JsonNode createPlaylist(String name) throws JsonProcessingException {
        Map<String, String> playlist = new HashMap<>();
        playlist.put("name", name);
        return mapper.readTree(restTemplate.postForObject("https://api.spotify.com/v1/me/playlists", playlist, String.class));
    }
}
