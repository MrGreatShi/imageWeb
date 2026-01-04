package org.example.imageweb.service;

import org.example.imageweb.dao.LabelDao;
import org.example.imageweb.entity.Label;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class LabelService {
    private final LabelDao labelDao;

    public LabelService() {
        this.labelDao = new LabelDao();
    }

    public List<Label> getLabelsOfImage(int image_id) throws Exception {
        return labelDao.getLabelsOfImage(image_id);
    }

    public List<Label> getLabelsOfUser(int user_id) throws Exception {
        return labelDao.getLabelsOfUser(user_id);
    }

    public void addLabelToImage(int image_id, int l_id) throws Exception {
        labelDao.attachLabel(image_id, l_id);
    }

    public void removeLabelFromImage(int image_id, int l_id) throws Exception {
        labelDao.detachLabel(image_id, l_id);
    }

    public void addLabelToUser(int user_id, String label) throws Exception {
        labelDao.addLabel(user_id, label);
    }

    public void removeLabelFromUser(int id) throws Exception {
        labelDao.deleteLabel(id);
    }
}
