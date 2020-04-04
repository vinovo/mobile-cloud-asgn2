package org.magnum.mobilecloud.video.client;

import com.google.common.collect.Lists;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;

@Controller
public class VideoSvcController {

    @Autowired
    private VideoRepository videoRepo;

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> getVideoList() {
        return Lists.newArrayList(videoRepo.findAll());
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method = RequestMethod.GET)
    public @ResponseBody Video getVideoById(@PathVariable long id, HttpServletResponse response) {
        Video v = videoRepo.findById(id);

        if (v == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return v;
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public @ResponseBody Video addVideo(@RequestBody Video v) {
        v.setLikes(0);

        return videoRepo.save(v);
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
    public Void likeVideo(@PathVariable long id, Principal p, HttpServletResponse response) {
        Video v = videoRepo.findById(id);

        if (v == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (v.getLikedBy().contains(p.getName())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        v.setLikes(v.getLikes() + 1);

        v.getLikedBy().add(p.getName());

        videoRepo.save(v);

        return null;
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
    public Void unlikeVideo(@PathVariable long id, Principal p, HttpServletResponse response) {
        Video v = videoRepo.findById(id);

        if (v == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        if (!v.getLikedBy().contains(p.getName())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }

        v.setLikes(v.getLikes() - 1);

        v.getLikedBy().remove(p.getName());

        videoRepo.save(v);

        return null;
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> findByTitle(@RequestParam String title) {
        return videoRepo.findByName(title);
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
    public @ResponseBody Collection<Video> findByDurationLessThan(@RequestParam long duration) {
        return videoRepo.findByDurationLessThan(duration);
    }

}
