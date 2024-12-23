package com.github.makewheels.aitools.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.word.Word07Writer;
import com.alibaba.fastjson.JSON;
import com.github.makewheels.aitools.file.FileService;
import com.github.makewheels.aitools.file.bean.CreateFileDTO;
import com.github.makewheels.aitools.file.constants.FileType;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.utils.OssPathUtil;
import com.github.makewheels.aitools.word.bean.Meaning;
import com.github.makewheels.aitools.word.bean.Word;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WordHelper {
    @Resource
    private IdService idService;
    @Resource
    private WordRepository wordRepository;
    @Resource
    private FileService fileService;

    public static final File BASE_FOLDER = new File("C:/Users/miuser/Downloads/word-folder");
    public static final File WORDS_FOLDER = new File(BASE_FOLDER, "words");

    private File getImageFile(File imageFolder, Meaning meaning) {
        return new File(imageFolder, meaning.getImagePromptMd5() + ".png");
    }

    /**
     * 导出json
     */
    public void export(List<Word> wordList, Map<String, String> imageMap) {
        for (Word word : wordList) {
            // 写json文件
            File wordFolder = new File(WORDS_FOLDER, word.getContent());
            File wordJsonFile = new File(wordFolder, word.getContent() + ".json");
            FileUtil.writeUtf8String(JSON.toJSONString(word, true), wordJsonFile);

            // 写图片文件
            File imageFolder = new File(wordFolder, "images");
            for (Meaning meaning : word.getMeanings()) {
                String imageUrl = MapUtils.getString(imageMap, meaning.getImagePromptMd5());
                if (StringUtils.isEmpty(imageUrl)) {
                    continue;
                }
                File imageFile = this.getImageFile(imageFolder, meaning);
                log.info("下载文件 " + imageFile.getName() + " " + imageUrl);
                HttpUtil.downloadFile(imageUrl, imageFile);
            }
        }

        // 压缩
        File zipFile = new File(BASE_FOLDER, System.currentTimeMillis() + ".zip");
        ZipUtil.zip(WORDS_FOLDER.getAbsolutePath(), zipFile.getAbsolutePath());
    }

    /**
     * 从磁盘读取为java对象
     */
    public List<Word> parse(File folder) {
        List<Word> wordList = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }
        for (File wordFolder : files) {
            File jsonFile = new File(wordFolder, wordFolder.getName() + ".json");
            wordList.add(JSON.parseObject(FileUtil.readUtf8String(jsonFile), Word.class));
        }
        return wordList;
    }

    /**
     * 写docx文件
     */
    public void writeDocFile(List<Word> wordList) {
        Word07Writer writer = new Word07Writer();

        Font fontBig = new Font("微软雅黑", Font.PLAIN, 22);
        Font fontSmall = new Font("微软雅黑", Font.PLAIN, 13);
        for (Word word : wordList) {
            File wordFolder = new File(WORDS_FOLDER, word.getContent());
            writer.addText(fontBig, word.getContent());
            writer.addText(fontSmall, word.getPronunciation());
            for (Meaning meaning : word.getMeanings()) {
                writer.addText(fontSmall, meaning.getPartOfSpeech() + meaning.getMeaningChinese());
                writer.addText(fontSmall, meaning.getExampleEnglish());
                writer.addText(fontSmall, meaning.getExampleChinese());
                File imageFolder = new File(wordFolder, "images");
                File imageFile = this.getImageFile(imageFolder, meaning);
                if (imageFile.exists()) {
                    writer.addPicture(imageFile, 192, 192);
                }
                writer.addText(fontSmall, "");
            }
            writer.addText(fontSmall, "");
        }

        writer.flush(new File(BASE_FOLDER, "doc/" + System.currentTimeMillis() + ".docx"));
        writer.close();
    }

    /**
     * 导入
     */
    public void importWords(MultipartFile multipartFile) throws IOException {
        log.info("开始导入单词, 上传文件大小 = " + FileUtil.readableFileSize(multipartFile.getSize()));
        // 解压
        File zipFile = FileUtil.createTempFile();
        multipartFile.transferTo(zipFile);
        File uncompressedFolder = new File(zipFile.getParentFile(), IdUtil.simpleUUID());
        ZipUtil.unzip(zipFile.getAbsolutePath(), uncompressedFolder.getAbsolutePath());
        FileUtil.del(zipFile);

        // 解析单词
        List<Word> wordList = this.parse(uncompressedFolder);

        // 保存到数据库
        for (Word word : wordList) {
            // 删除老的
            Word oldWord = wordRepository.getByContent(word.getContent());
            for (Meaning meaning : oldWord.getMeanings()) {
                if (StringUtils.isEmpty(meaning.getImageFileId())){
                    continue;
                }
                fileService.deleteFile(meaning.getImageFileId());
            }
            wordRepository.delete(oldWord.getId());

            // 插入新的
            word.setId(idService.getWordId());
            word.setCreateTime(new Date());
            wordRepository.save(word);

            // 上传文件到对象存储
            for (Meaning meaning : word.getMeanings()) {
                CreateFileDTO createFileDTO = new CreateFileDTO();
                createFileDTO.setFileType(FileType.WORD_IMAGE);
                createFileDTO.setExtension("png");
                createFileDTO.setKey(OssPathUtil.getWordImageFile(word.getContent(), meaning.getImagePromptMd5()));

                String imageFileId = fileService.createNewFile(createFileDTO).getId();
                meaning.setImageFileId(imageFileId);
                wordRepository.save(word);

                File imageFolder = new File(uncompressedFolder, word.getContent() + "/images");
                fileService.uploadFile(imageFileId, this.getImageFile(imageFolder, meaning));

                fileService.uploadFinish(imageFileId);
            }
        }

        FileUtil.del(uncompressedFolder);
        log.info("单词导入完成");
    }

}
