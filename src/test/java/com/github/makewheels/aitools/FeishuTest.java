package com.github.makewheels.aitools;

import com.lark.oapi.Client;
import com.lark.oapi.service.drive.v1.model.Owner;
import com.lark.oapi.service.drive.v1.model.TransferOwnerPermissionMemberReq;
import com.lark.oapi.service.sheets.v3.model.CreateSpreadsheetReq;
import com.lark.oapi.service.sheets.v3.model.CreateSpreadsheetResp;
import com.lark.oapi.service.sheets.v3.model.Spreadsheet;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class FeishuTest {
    private Client getClient() {
        return Client.newBuilder("cli_a7ed3d46957ad013", "")
                .requestTimeout(3, TimeUnit.SECONDS) // 设置httpclient 超时时间，默认永不超时
                .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers、body 等信息。
                .build();
    }

    private String createSheet() throws Exception {
        // 创建请求对象
        CreateSpreadsheetReq req = CreateSpreadsheetReq.newBuilder()
                .spreadsheet(Spreadsheet.newBuilder()
                        .title("Sales sheet")
//                        .folderToken("LVdpfhgPnlWE3vd2NzhcZ5genxe")
                        .build())
                .build();

        // 发起请求
        CreateSpreadsheetResp resp = this.getClient().sheets().spreadsheet().create(req);

        return resp.getData().getSpreadsheet().getSpreadsheetToken();
    }

    private void movePermission(String token) throws Exception {
        // 创建请求对象
        TransferOwnerPermissionMemberReq req = TransferOwnerPermissionMemberReq.newBuilder()
                .type("sheet")
                .token(token)
                .owner(Owner.newBuilder()
                        .memberType("openid")
                        .memberId("ou_16f5e671ef0ecea8e5dce081549c5958")
                        .build())
                .build();

        // 发起请求
        this.getClient().drive().permissionMember().transferOwner(req);
    }

    @Test
    public void test() throws Exception {
        String token = this.createSheet();
        this.movePermission(token);
    }

}
