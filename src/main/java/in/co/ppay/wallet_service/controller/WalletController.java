package in.co.ppay.wallet_service.controller;

import in.co.ppay.wallet_service.entity.Wallet;
import in.co.ppay.wallet_service.exception.WalletNotFoundException;
import in.co.ppay.wallet_service.service.WalletService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // find all wallet
    @ApiOperation(value = "Find all the wallet")
    @GetMapping("/admin/findAllWallet")
    List<Wallet> findAllWallet() {
        return walletService.findAll();
    }

    // Find a given wallet
    @ApiOperation(value = "Find wallet by mobile ")
    @GetMapping("/admin/wallet/{mobile}")
    Wallet findOneWallet(@ApiParam(
            value = "Store id of the point of service to deliver to/collect from")
                         @PathVariable("mobile") String mobile) {
        log.info("/wallet/{id} called with id " + mobile);
        Wallet wallet = walletService.findByMobile(mobile);
        if (wallet == null)
            throw new WalletNotFoundException(mobile);
        else return wallet;
    }
}
