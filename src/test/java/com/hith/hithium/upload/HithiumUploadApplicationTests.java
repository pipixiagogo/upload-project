package com.hith.hithium.upload;

import com.hith.hithium.upload.common.FileDocumentType;
import com.hith.hithium.upload.entity.*;
import com.mongodb.client.gridfs.GridFSBucket;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class HithiumUploadApplicationTests {
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private GridFsTemplate gridFsTemplate;
    @Resource
    private GridFSBucket gridFSBucket;

    @Test
    public void contextLoads() throws Exception {

//        Set<String> fileTypes = FileTypeEnum.getFileTypes();
//        for (String file : fileTypes) {
//            System.out.println(file);
//        }
//        File file = new File("d:\\Users\\zhouh\\Desktop\\微信截图_20211103085712.png");
        File file = new File("d:\\Users\\zhouh\\Desktop\\1.4-SpringBoot_v2.0.pdf");

        FileDocumentType fileDocumentType = FileDocumentType.getFileType(new FileInputStream(file));
        System.out.println(fileDocumentType);
        System.out.println(FileDocumentType.PDF);
//        System.out.println(fileType.equals(FileType.PNG));
//        System.out.println(FileType.PNG);

//        boolean contains = fileTypes.containsAll(msgs);
//        System.out.println(contains);


//        Map<FileTypeEnum, String> fileTypesMap = FileTypeEnum.getFileTypesMap();
//        Set<Map.Entry<FileTypeEnum, String>> entries = fileTypesMap.entrySet();
//        for(Map.Entry<FileTypeEnum, String> entry:entries){
//            System.out.println(entry.getKey()+"---"+entry.getValue());
//        }
//        String sys="SYSTEM";
//        String fileTypesByEnum = FileTypeEnum.getFileTypesByEnum(sys);
//        System.out.println(fileTypesByEnum);

    }

    @Test
    public void testInsert() {
//        ProjectInfo projectInfo =new ProjectInfo();
//        projectInfo.setProjectNum("bbccddee");
//        projectInfo.setProjectName("ppoossddff");
//        projectInfo.setProjectCreateDate(new Date(System.currentTimeMillis()));
//        mongoTemplate.insert(projectInfo);

//        Query query = new Query();
//        Criteria criteria = new Criteria();
//        criteria.orOperator(Criteria.where("projectNum").regex(".*?BB.*", "i"), Criteria.where("projectName").regex(".*?aa.*", "i"));
////        query.addCriteria(Criteria.where("projectNum").regex());
//        query.addCriteria(criteria);
//
//        List<ProjectInfo> projectInfos = mongoTemplate.find(query, ProjectInfo.class);
//        System.out.println(projectInfos.size());
    }

    @Test
    public void testPassword() {
//        FileEntity fileEntity = new FileEntity();
//        Entity entity = new Entity();
//        entity.setEntity("test,xxx,aaa");
//        fileEntity.setEntity(entity);
//        mongoTemplate.insert(fileEntity);

//        Query query = new Query();
//        query.addCriteria(Criteria.where("entity.entity").is("test,xxx,aaa"));
//        query.addCriteria(Criteria.where("id").is("617791ea04612f66dac1782e"));
//        List<FileEntity> fileEntities = mongoTemplate.find(query, FileEntity.class);
//        System.out.println(fileEntities.size());

        Query query = new Query();
        query.addCriteria(Criteria.where("entityList.id").is("1"));

        Update update = new Update();
        update.unset("entityList.$");
        long modifiedCount = mongoTemplate.updateMulti(query, update, FileEntity.class).getModifiedCount();
        System.out.println(modifiedCount);

        List<FileEntity> fileEntities = mongoTemplate.find(new Query(), FileEntity.class);
        System.out.println(fileEntities);
        for (FileEntity fileEntity : fileEntities) {
            if (!fileEntity.getEntityList().isEmpty() && fileEntity.getEntityList().size() > 0) {
                List<ListEntity> entityList = fileEntity.getEntityList();
                System.out.println("未筛选后的数据:" + entityList);
                List<ListEntity> listEntities = fileEntity.getEntityList().stream().filter(fileEntityV -> fileEntityV != null).collect(Collectors.toList());
                System.out.println("筛选后的数据:" + listEntities.size());
            }
        }
//        ListEntity listEntity = new ListEntity();
//        ListEntity listEntity1 = new ListEntity();
//        listEntity.setName("张三");
//        listEntity.setId("1");
//        listEntity1.setName("李四");
//        listEntity1.setId("2");
//        List<ListEntity> listEntities = new ArrayList<>();
//        listEntities.add(listEntity);
//        listEntities.add(listEntity1);
//        FileEntity fileEntity = new FileEntity();
//        fileEntity.setEntityList(listEntities);
//        mongoTemplate.insert(fileEntity);
    }

    @Test
    public void testSearchMore() {
        String searchText = "CC";
        Query query = new Query();
        List<String> check = new ArrayList<>();
        String[] split = searchText.split(",");
        for (String sp : split) {
            if (!StringUtils.isEmpty(sp)) {
                check.add(sp);
            }
        }
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if (check != null && !check.isEmpty() && check.size() > 0) {
            for (String che : check) {
                criteriaList.add(Criteria.where("uploadCustomerName").regex(che));
                criteriaList.add(Criteria.where("prefix").regex(che));
                criteriaList.add( Criteria.where("projectInfo.projectName").regex(che));
                criteriaList.add(Criteria.where("fileType.fileType").regex(che));
            }
        }
        criteria.orOperator(criteriaList);
        query.addCriteria(criteria);

        List<FileDocument> fileDocumentList = mongoTemplate.find(query, FileDocument.class);
        System.out.println(fileDocumentList);
    }
    @Test
    public void testRemoveFile(){
        Customer customer= new Customer();
        customer.setEmployeeNum("000000");
        customer.setCustomerName("管理员");
        customer.setDepartment("ESD");
        customer.setOnTheJob(true);
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        customer.setPassword(new Sha256Hash(customer.getEmployeeNum(), salt).toHex());
        customer.setSalt(salt);
        customer.setPermissions("sys:power:customer,sys:power:fileDocument,sys:power:fileType,sys:power:projectInfo");
        System.out.println(customer);
    }
}
